package com.example.bellmusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class MusicPlayer extends AppCompatActivity {

    String entry_path;
    MediaPlayer player;
    ImageButton start_pause_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_music);
        entry_path = Objects.requireNonNull(getIntent().getExtras()).getString("entry");

        // load image
        Bitmap img = BitmapFactory.decodeFile(entry_path + File.separator + "thumbnail");
        ImageView img_view = findViewById(R.id.song_img);
        img_view.setImageBitmap(img);

        // load song
        // TODO check if same song
        Uri song = Uri.fromFile(new File(entry_path + File.separator + "audio"));
        if (MainActivity.player != null && MainActivity.player.isPlaying()){
            MainActivity.player.stop();
        }
        player = MediaPlayer.create(getApplicationContext(), song);
        MainActivity.player = player;
        player.start();
        start_pause_button = findViewById(R.id.start_pause);
    }

    public void onStartPause(View view){
        if (player.isPlaying()){
            player.pause();
            start_pause_button.setImageResource(android.R.drawable.ic_media_play);
        }else{
            player.start();
            start_pause_button.setImageResource(android.R.drawable.ic_media_pause);
        }
    }
}
