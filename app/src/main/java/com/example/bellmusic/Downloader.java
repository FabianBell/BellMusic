package com.example.bellmusic;


import android.view.View;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;

public class Downloader implements Runnable {

    // TODO include Downloader in AddMusic
    private Callback<YoutubeVideo, Void> on_ready;
    private Callback<Exception, Void> on_error;
    private String video_id;

    Downloader(Callback<YoutubeVideo, Void> on_ready, Callback<Exception, Void> on_error,
               String video_id){
        this.on_ready = on_ready;
        this.on_error = on_error;
        this.video_id = video_id;
    }

    @Override
    public void run() {
        YoutubeDownloader downloader = new YoutubeDownloader();
        try {
            YoutubeVideo video = downloader.getVideo(video_id);
            this.on_ready.call(video);
        } catch (YoutubeException e) {
            this.on_error.call(e);
        }
    }
}
