package com.anray.musicapp.data.storage.models;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.anray.musicapp.data.storage.models.Mp3File;

import com.anray.musicapp.data.storage.models.Mp3FileDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig mp3FileDaoConfig;

    private final Mp3FileDao mp3FileDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        mp3FileDaoConfig = daoConfigMap.get(Mp3FileDao.class).clone();
        mp3FileDaoConfig.initIdentityScope(type);

        mp3FileDao = new Mp3FileDao(mp3FileDaoConfig, this);

        registerDao(Mp3File.class, mp3FileDao);
    }
    
    public void clear() {
        mp3FileDaoConfig.getIdentityScope().clear();
    }

    public Mp3FileDao getMp3FileDao() {
        return mp3FileDao;
    }

}
