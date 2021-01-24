package com.example.bellmusic;


public class BackgroundThread<T> implements Runnable {

    private Callback<T, Void> func;
    private T param;

    BackgroundThread(Callback<T, Void> func, T param){
        this.func = func;
        this.param = param;
    }

    @Override
    public void run() {
        this.func.call(this.param);
    }
}
