package com.anray.musicapp.managers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.anray.musicapp.MusicApplication;

/**
 * Created by anray on 14.08.2016.
 */
public class DataManager {
    private static DataManager INSTANCE = new DataManager();

    private Context mContext;


    public DataManager() {
        this.mContext = MusicApplication.getContext();


    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public static void writeLog(String TAG, Object message) {
        if (true) {
            Log.d(TAG, message.toString());
        }
    }

    public static void writeLog(Object message) {
        if (true) {
            Log.d("Fixed TAG", message.toString());
        }
    }

    public static void showToast(String message) {
        Toast.makeText(MusicApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
