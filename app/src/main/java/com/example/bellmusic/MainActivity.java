package com.example.bellmusic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    LinearLayout list;
    File music_dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.landing_name);

        list = findViewById(R.id.music_list);
        music_dir = new File(getFilesDir() + File.separator + "music");
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.removeAllViews();
        new Thread(this::loadEntries).start();
    }

    private void loadEntries(){
        if (!music_dir.exists()){
            return;
        }
        File[] entry_dirs = music_dir.listFiles();
        assert entry_dirs != null;
        boolean first = true;
        for (File entry_dir : entry_dirs){
            if (!first){
                View line = Utils.buildSeparator(this);
                runOnUiThread(() -> list.addView(line));
            }
            View entry = Utils.buildEntry(this, entry_dir);
            runOnUiThread(() -> list.addView(entry));
            first = false;

            // add on click listener
            entry.setOnClickListener(view -> new Thread(() -> onEntryClick(entry_dir.getPath())).start());
        }
    }

    private void onEntryClick(String dir){
        System.out.println(dir);
    }

    public void loadAddMusic(View view){
        Intent intent = new Intent(this, AddMusic.class);
        startActivity(intent);
    }
}