package com.example.bellmusic;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Objects;

public class Utils {

    @SuppressLint("InflateParams")
    public static View buildSeparator(AppCompatActivity parent){
        LayoutInflater inflater = parent.getLayoutInflater();
        return inflater.inflate(R.layout.line, null);
    }

    @SuppressLint({"InflateParams", "ResourceAsColor"})
    public static View buildEntry(AppCompatActivity parent, File entry_dir){
        // create entry layout
        LayoutInflater inflater = parent.getLayoutInflater();
        View entry = inflater.inflate(R.layout.music_entry, null);

        // load and add thumbnail
        Bitmap thumbnail = BitmapFactory.decodeFile(entry_dir.getPath() + File.separator + "thumbnail");
        ImageView thumbnail_view = entry.findViewById(R.id.entry_thumbnail);
        thumbnail_view.setImageBitmap(thumbnail);

        // load meta data
        String title;
        String author;
        String duration;
        try {
            FileReader reader = new FileReader(entry_dir.getPath() + File.separator + "meta_data");
            JSONParser parser = new JSONParser();
            JSONObject meta_data = (JSONObject) parser.parse(reader);
            title = (String) meta_data.get("title");
            author = (String) meta_data.get("author");
            int length = ((Long) Objects.requireNonNull(meta_data.get("length"))).intValue();
            duration = secToTime(length);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read meta dara from " + entry_dir.getPath());
        }

        // add meta data
        TextView title_view = entry.findViewById(R.id.entry_title);
        title_view.setText(title);
        TextView author_view = entry.findViewById(R.id.entry_author);
        author_view.setText(author);
        TextView duration_view = entry.findViewById(R.id.entry_time);
        duration_view.setText(duration);

        return entry;
    }

    public static void downloadFromURL(String url_path, String filePath){
        try {
            URL url = new URL(url_path);
            InputStream in = new BufferedInputStream(url.openStream());
            OutputStream out = new FileOutputStream(filePath);
            byte[] buf = new byte[1024];
            int b;
            while (-1 != (b = in.read(buf))){
                out.write(buf, 0, b);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot read from " + url_path + " and save it ot " + filePath);
        }
    }

    public static void deleteDir(File dir){
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files){
                deleteDir(file);
            }
        }
        if (!dir.delete()){
            throw new RuntimeException("Cannot delete directory " + dir);
        }
    }

    @SuppressWarnings("unused")
    public static Void nothing(Object obj){
        return null;
    }

    @SuppressLint("DefaultLocale")
    public static String secToTime(int sec) {
        int hours = sec / 3600;
        int minutes = (sec % 3600) / 60;
        sec = sec % 60;
        String time;
        if (hours != 0) {
            time = String.format("%d:%02d:%02d", hours, minutes, sec);
        } else {
            time = String.format("%02d:%02d", minutes, sec);
        }
        return time;
    }
}
