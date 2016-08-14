package com.anray.musicapp.data.storage.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by anray on 14.08.2016.
 */
@Entity(active = true, nameInDb = "MP3_FILES")
public class Mp3File {

    @Id
    private Long id;

    @NotNull
    private int order;

    //0 - not playing, 1 - is playing
    @NotNull
    private int playingFlag;

    @Unique
    @NotNull
    private String fullPath;

    private String title;

    private String artist;

    /** Used for active entity operations. */
    @Generated(hash = 328864216)
    private transient Mp3FileDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;


    public Mp3File(String fullPath, int order, String title, String artist) {
        this.order = order;
        this.playingFlag = 0;
        this.fullPath = fullPath;
        this.title = title;
        this.artist = artist;
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }


    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 672139047)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMp3FileDao() : null;
    }


    public String getArtist() {
        return this.artist;
    }


    public void setArtist(String artist) {
        this.artist = artist;
    }


    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getFullPath() {
        return this.fullPath;
    }


    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }


    public int getPlayingFlag() {
        return this.playingFlag;
    }


    public void setPlayingFlag(int playingFlag) {
        this.playingFlag = playingFlag;
    }


    public int getOrder() {
        return this.order;
    }


    public void setOrder(int order) {
        this.order = order;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    @Generated(hash = 1969286879)
    public Mp3File(Long id, int order, int playingFlag, @NotNull String fullPath,
            String title, String artist) {
        this.id = id;
        this.order = order;
        this.playingFlag = playingFlag;
        this.fullPath = fullPath;
        this.title = title;
        this.artist = artist;
    }


    @Generated(hash = 1101670431)
    public Mp3File() {
    }
}
