package com.anray.musicapp;

import android.app.Application;
import android.content.Context;

import com.anray.musicapp.data.storage.models.DaoMaster;
import com.anray.musicapp.data.storage.models.DaoSession;
import com.facebook.stetho.Stetho;

import org.greenrobot.greendao.database.Database;

/**
 * Created by anray on 14.08.2016.
 */
public class MusicApplication extends Application {

    private static Context sContext;

    private static DaoSession sDaoSession;


    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(sContext, "MusicApp-db");
        Database db = helper.getWritableDb();
        sDaoSession = new DaoMaster(db).newSession();

        Stetho.initializeWithDefaults(sContext);

    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }
}
