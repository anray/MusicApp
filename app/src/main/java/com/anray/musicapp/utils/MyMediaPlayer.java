package com.anray.musicapp.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by anray on 16.08.2016.
 */
public class MyMediaPlayer extends MediaPlayer {

    private String dataSource;

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        super.setDataSource(path);

        this.dataSource = path;

    }

    public String getDataSource() {
        return dataSource;
    }

}
