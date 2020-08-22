package com.example.bellmusic.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bellmusic.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Utils {

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
