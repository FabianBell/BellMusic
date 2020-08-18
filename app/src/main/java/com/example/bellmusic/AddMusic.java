package com.example.bellmusic;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AddMusic extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_music);

        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.add_music_name);
        bar.setDisplayHomeAsUpEnabled(true);
    }
}
