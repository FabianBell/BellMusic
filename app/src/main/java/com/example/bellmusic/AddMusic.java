package com.example.bellmusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kiulian.downloader.OnYoutubeDownloadListener;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioFormat;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddMusic extends AppCompatActivity {

    private Pattern url_pattern;

    private static final List<String> audio_rank = Arrays.asList("high", "medium", "low");

    private ProgressBar loading;
    private ProgressBar progress;

    // TODO check if stop interrupts the download

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_music);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(R.string.add_music_name);
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);

        //TODO: check pattern
        url_pattern = Pattern.compile("https://www\\.youtube\\.com/watch\\?v=(?<code>[^&(http)\\s]*)");

        loading = findViewById(R.id.add_music_loading);
        progress = findViewById(R.id.add_music_progress);
    }

    private Void onReady(String video_id) {
        // get video info
        YoutubeDownloader downloader = new YoutubeDownloader();
        YoutubeVideo video;
        try {
            video = downloader.getVideo(video_id);
        } catch (YoutubeException e) {
            this.onError(e);
            return null;
        }

        // deactivate loading
        runOnUiThread(() -> loading.setVisibility(View.GONE));

        // check available audio formats
        List<AudioFormat> formats = video.audioFormats();

        File audio_dir = new File(getFilesDir().getPath() + File.separator + "music");
        if (!audio_dir.exists() && !audio_dir.mkdir()){
            throw new RuntimeException("Cannot crate dir at " + audio_dir.getPath());
        }

        VideoDetails details = video.details();

        // check and prepare target space on device
        File target_dir = new File(audio_dir.getPath() + File.separator + details.videoId());
        if (target_dir.exists()){
            OptionDialog dialog = new OptionDialog(
                    view -> {
                        TextView text = view.findViewById(R.id.fragment_text);
                        text.setText(R.string.double_entry);
                        return view;
                    },
                    null, Utils::nothing, R.string.alert, R.layout.smiple_text,
                    R.string.question_ok, -1);
            dialog.show_dialog(this);
            return null;
        }
        if (!target_dir.mkdir()){
            throw new RuntimeException("Cannot create dir at " + target_dir.getPath());
        }

        // download thumbnail
        Bitmap thumbnail = downloadDetails(details, target_dir);

        OptionDialog dialog = new OptionDialog(
                view -> {
                    // build entry
                    TextView title = view.findViewById(R.id.entry_title);
                    title.setText(details.title());
                    TextView author = view.findViewById(R.id.entry_author);
                    author.setText(details.author());
                    ImageView img = view.findViewById(R.id.entry_thumbnail);
                    img.setImageBitmap(thumbnail);
                    TextView time = view.findViewById(R.id.entry_time);
                    time.setText(Utils.secToTime(details.lengthSeconds()));
                    return view;
                },
                inp -> {
                    // delete target directory
                    Utils.deleteDir(target_dir);
                    return null;
                },
                inp -> {
                    // start audio download
                    download_audio(formats, video, target_dir);
                    return null;
                }, R.string.add_new_song_popup_title, R.layout.music_entry
        );
        dialog.show_dialog(this);

        return null;
    }

    @SuppressWarnings("unchecked")
    private Bitmap downloadDetails(VideoDetails details, File target_dir){
        // download thumbnail
        String thumbnail_url = details.thumbnails().get(0);
        String thumbnail_path = target_dir.getPath() + File.separator + "thumbnail";
        Utils.downloadFromURL(thumbnail_url, thumbnail_path);
        Bitmap thumbnail = BitmapFactory.decodeFile(thumbnail_path);

        // store meta data
        JSONObject meta_data = new JSONObject();
        meta_data.put("title", details.title());
        meta_data.put("author", details.author());
        meta_data.put("length", details.lengthSeconds());
        try {
            Files.write(Paths.get(target_dir.getPath() + File.separator + "meta_data"),
                    meta_data.toJSONString().getBytes());
        } catch (IOException e) {
            onDownloadError(e);
        }
        return thumbnail;
    }

    private void download_audio(List<AudioFormat> formats, YoutubeVideo video, File target_dir){
        // activate loading and deactivate other elements
        runOnUiThread(() -> {
            progress.setVisibility(View.VISIBLE);
            findViewById(R.id.url).setVisibility(View.GONE);
            findViewById(R.id.download_url).setVisibility(View.GONE);
        });

        // select audio quality
        Optional<AudioFormat> o_format = formats.stream().reduce((f1, f2) ->
                audio_rank.indexOf(
                        f1.audioQuality().name()) > audio_rank.indexOf(f2.audioQuality().name())
                        ? f2 : f1);
        if (!o_format.isPresent()){
            throw new RuntimeException("Unknown Audio Format");
        }
        AudioFormat format = o_format.get();

        // download audio
        try {
            video.downloadAsync(format, target_dir, new OnYoutubeDownloadListener() {

                // TODO add time estimation
                @Override
                public void onDownloading(int p) {
                    runOnUiThread(() -> progress.setProgress(p));
                }

                @Override
                public void onFinished(File file) {
                    // rename to default name
                    if (!file.renameTo(new File(target_dir + File.separator + "audio"))){
                        throw new RuntimeException("Cannot rename file from " + file.getName() + " to audio");
                    }
                    // terminate activity
                    terminate();
                }

                @Override
                public void onError(Throwable throwable) {
                    onDownloadError(throwable);
                }
            });
        } catch (IOException | YoutubeException e) {
            onDownloadError(e);
        }
    }

    private void terminate(){
        runOnUiThread(this::finish);
    }

    private void onDownloadError(Throwable e){
        e.printStackTrace();
        throw new RuntimeException("Cannot download video");
    }

    private void onError(Exception e){
        runOnUiThread(() -> loading.setVisibility(View.GONE));
        e.printStackTrace();
        TextView err_field = findViewById(R.id.err_msg);
        err_field.setText(R.string.url_err_msg2);
    }

    public void download(View view){
        // parse url
        TextView url_field = findViewById(R.id.url);
        String url = url_field.getText().toString();
        Matcher matcher = url_pattern.matcher(url);
        if (!matcher.find()){
            TextView err_field = findViewById(R.id.err_msg);
            err_field.setText(R.string.url_err_msg1);
            return;
        }
        String video_code = matcher.group("code");
        System.out.println("DEBUG: " + video_code);

        // get video information
        Thread downloader = new Thread(new BackgroundThread<>(this::onReady, video_code));
        loading.setVisibility(View.VISIBLE);
        downloader.start();
    }
}
