package com.example.bellmusic;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioFormat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddMusic extends AppCompatActivity {

    Pattern url_pattern;

    private static final List<String> audio_rank = Arrays.asList("high", "medium", "low");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_music);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(R.string.add_music_name);
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);

        url_pattern = Pattern.compile("https://www\\.youtube\\.com/watch\\?v=(?<code>[^&]*)(&\\w*)*");
    }

    private Void onDownload(YoutubeVideo video) {
        List<AudioFormat> formats = video.audioFormats();
        Optional<AudioFormat> o_format = formats.stream().reduce((f1, f2) ->
                audio_rank.indexOf(
                        f1.audioQuality().name()) > audio_rank.indexOf(f2.audioQuality().name())
                        ? f2 : f1);
        if (!o_format.isPresent()){
            throw new RuntimeException("Unknown Audio Format");
        }
        AudioFormat format = o_format.get();
        File audio_dir = new File(getFilesDir().getPath() + File.separator + "music");
        if (!audio_dir.exists() && !audio_dir.mkdir()){
            throw new RuntimeException("Cannot crate dir at " + audio_dir.getPath());
        }
        try {
            System.out.println("Start download");
            // TODO check if path exists
            File audio_file = video.download(format, audio_dir);
            System.out.println(audio_file.getAbsolutePath());
        } catch (IOException | YoutubeException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot download video");
        }
        return null;
    }

    private Void onError(Exception e){
        e.printStackTrace();
        TextView err_field = findViewById(R.id.err_msg);
        err_field.setText(R.string.url_err_msg2);
        return null;
    }

    public void download(View view){
        // parse url
        // TODO ignore double urls
        TextView url_field = findViewById(R.id.url);
        String url = url_field.getText().toString();
        Matcher matcher = url_pattern.matcher(url);
        if (!matcher.find()){
            TextView err_field = findViewById(R.id.err_msg);
            err_field.setText(R.string.url_err_msg1);
            return;
        }
        String video_code = matcher.group("code");

        // get video information
        Thread downloader = new Thread(new Downloader(
                this::onDownload,
                this::onError,
                video_code
        ));
        downloader.start();
    }
}
