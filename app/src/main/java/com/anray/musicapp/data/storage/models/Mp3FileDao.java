package com.anray.musicapp.data.storage.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MP3_FILES".
*/
public class Mp3FileDao extends AbstractDao<Mp3File, Long> {

    public static final String TABLENAME = "MP3_FILES";

    /**
     * Properties of entity Mp3File.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Order = new Property(1, int.class, "order", false, "ORDER");
        public final static Property PlayingFlag = new Property(2, int.class, "playingFlag", false, "PLAYING_FLAG");
        public final static Property FullPath = new Property(3, String.class, "fullPath", false, "FULL_PATH");
        public final static Property Title = new Property(4, String.class, "title", false, "TITLE");
        public final static Property Artist = new Property(5, String.class, "artist", false, "ARTIST");
    };

    private DaoSession daoSession;


    public Mp3FileDao(DaoConfig config) {
        super(config);
    }
    
    public Mp3FileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MP3_FILES\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ORDER\" INTEGER NOT NULL ," + // 1: order
                "\"PLAYING_FLAG\" INTEGER NOT NULL ," + // 2: playingFlag
                "\"FULL_PATH\" TEXT NOT NULL UNIQUE ," + // 3: fullPath
                "\"TITLE\" TEXT," + // 4: title
                "\"ARTIST\" TEXT);"); // 5: artist
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MP3_FILES\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Mp3File entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getOrder());
        stmt.bindLong(3, entity.getPlayingFlag());
        stmt.bindString(4, entity.getFullPath());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(5, title);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(6, artist);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Mp3File entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getOrder());
        stmt.bindLong(3, entity.getPlayingFlag());
        stmt.bindString(4, entity.getFullPath());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(5, title);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(6, artist);
        }
    }

    @Override
    protected final void attachEntity(Mp3File entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Mp3File readEntity(Cursor cursor, int offset) {
        Mp3File entity = new Mp3File( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // order
            cursor.getInt(offset + 2), // playingFlag
            cursor.getString(offset + 3), // fullPath
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // title
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // artist
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Mp3File entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOrder(cursor.getInt(offset + 1));
        entity.setPlayingFlag(cursor.getInt(offset + 2));
        entity.setFullPath(cursor.getString(offset + 3));
        entity.setTitle(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setArtist(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Mp3File entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Mp3File entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
