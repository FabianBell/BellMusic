package com.example.bellmusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bellmusic.Dialog.OptionDialog;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.landing_name);
    }

    public void loadAddMusic(View view){
        Intent intent = new Intent(this, AddMusic.class);
        startActivity(intent);
    }

    @SuppressLint("InflateParams")
    public void test(View b_view){
        OptionDialog dialog = new OptionDialog(
                view -> {
                    TextView title = view.findViewById(R.id.entry_title);
                    title.setText("TEST TITLE");
                    TextView author = view.findViewById(R.id.entry_author);
                    author.setText("Fabian Bell");
                    return view;
                },
                inp -> {
                    System.out.println("NO");
                    return null;
                    },
                inp -> {
                    System.out.println("Yes");
                    return null;
                    },
                R.string.add_new_song_popup_title,
                R.layout.music_entry);
        dialog.show(getSupportFragmentManager(), "download dialog");
    }
}