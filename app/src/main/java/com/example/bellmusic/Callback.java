package com.example.bellmusic;

public interface Callback<I,O> {

    public O call(I inp);
}
