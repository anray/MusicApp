package com.anray.musicapp.activities;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.anray.musicapp.MusicApplication;
import com.anray.musicapp.R;
import com.anray.musicapp.data.storage.models.Mp3File;
import com.anray.musicapp.data.storage.models.Mp3FileDao;
import com.anray.musicapp.managers.DataManager;
import com.anray.musicapp.ui.adapters.FileListAdapter;
import com.anray.musicapp.utils.MyMediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "MainActivity";
    private static final String PLAYING_SONG_PATH = "PLAYING_SONG_PATH";
    private static final String PLAYING_STATE = "PLAYING_STATE";
    private static final String PLAYING_POSITION = "PLAYING_POSITION";
    private static final String PLAYING_SONG_ORDER = "PLAYING_SONG_ORDER";


    private Button mButton;
    private ImageView mMainCoverImage, mPlay, mNext, mPrevious;

    private List<Mp3File> mMp3FilesList;

    private RecyclerView mRecyclerView;
    private FileListAdapter mFileListAdapter;

    private MyMediaPlayer mMediaPlayer;


    private int mPlayingState = 0;
    private int mCurrentPlayingSongPosition, mCurrentPlayingSongOrder, mSongsInPlaylist;

    private String mCurrentPlayingSongPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.choose_btn);
        mButton.setOnClickListener(this);

        mMainCoverImage = (ImageView) findViewById(R.id.image_iv);
        mPlay = (ImageView) findViewById(R.id.play_iv);
        mNext = (ImageView) findViewById(R.id.next_iv);
        mPrevious = (ImageView) findViewById(R.id.previous_iv);
        mPlay.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_of_files_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMediaPlayer = new MyMediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMp3FilesList = loadFromDb();
        //need to current number of tracks
        mSongsInPlaylist = mMp3FilesList.size();

        //если база не пуста
        if (mMp3FilesList.size() > 0) {
            showSongsList(mMp3FilesList);

            //если бандл пуст, берем первую песню из БД
            if (savedInstanceState == null) {

                mCurrentPlayingSongOrder = 1;
                mCurrentPlayingSongPath = loadSongByOrder(mCurrentPlayingSongOrder);

                setSongInMediaPlayer(mCurrentPlayingSongPath);

                DataManager.writeLog("savedInstanceState is null");

            } else {

                DataManager.writeLog("savedInstanceState NOT null");
                mCurrentPlayingSongPath = savedInstanceState.getString(PLAYING_SONG_PATH);
                mCurrentPlayingSongPosition = savedInstanceState.getInt(PLAYING_POSITION);
                mCurrentPlayingSongOrder = savedInstanceState.getInt(PLAYING_SONG_ORDER);
                mPlayingState = savedInstanceState.getInt(PLAYING_STATE);

                setSongInMediaPlayer(mCurrentPlayingSongPath);
                mMediaPlayer.seekTo(mCurrentPlayingSongPosition);
                restorePlayingState();

            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mCurrentPlayingSongPath = mMediaPlayer.getDataSource();
        outState.putString(PLAYING_SONG_PATH, mCurrentPlayingSongPath);

        mCurrentPlayingSongPosition = mMediaPlayer.getCurrentPosition();
        outState.putInt(PLAYING_POSITION, mCurrentPlayingSongPosition);

        outState.putInt(PLAYING_STATE, mPlayingState);

        outState.putInt(PLAYING_SONG_ORDER, mCurrentPlayingSongOrder);

    }

    @Override
    protected void onPause() {
        super.onPause();
        DataManager.writeLog(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataManager.writeLog(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
        DataManager.writeLog(TAG, "onDestroy");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_btn:
                showFileChooser();
                break;
            case R.id.play_iv:
                playButtonAction();
                break;
            case R.id.next_iv:
                skipNext();
                break;
            case R.id.previous_iv:
                skipPrev();
                break;
        }
    }


    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg4-generic");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to choose encapsulating Folder"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    // Gets from Intent the Uri of the selected file
                    // URI looks like - file:///storage/emulated/0/Audio/Pop/Armin%20Van%20Buuren%20-%20In%20And%20Out%20Of%20Love(feat.%20Sharon%20den%20Adel).mp3
                    Uri uri = data.getData();
                    DataManager.writeLog(TAG, uri);


                    // get Path without schema - /storage/emulated/0/Audio/Pop/Armin Van Buuren - In And Out Of Love(feat. Sharon den Adel).mp3
                    File chosenFile = new File(uri.getPath());
                    DataManager.writeLog(TAG, uri.getPath());


                    // get ONLY path without fileName - /storage/emulated/0/Audio/Pop
                    chosenFile = chosenFile.getParentFile();
                    //getParentFile() moves one step back in hierarchy
                    //DataManager.writeLog(TAG, chosenFile.getParentFile());

                    final String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {

                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mMp3FilesList = new ArrayList<>();
                        File[] files = chosenFile.listFiles();
                        String artist = "";
                        String title = "";

                        //need separate index because some mp3 file may be corrupted
                        int index = 1;
                        for (int i = 0; i < files.length; i++) {


                            DataManager.writeLog(TAG, files[i]);
                            try {
                                //need because some mp3 file may be corrupted
                                mmr.setDataSource(files[i].toString());
                                artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                                title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }

                            mMp3FilesList.add(new Mp3File(files[i].toString(), index, title, artist));
                            index++;

                        }
                        mmr.release();
                        new MyTaskForSavingToDb().execute();


                    }

                } else {
                    DataManager.showToast("The Storage is not ready!");
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSongsList(List<Mp3File> mp3FilesList) {


        mFileListAdapter = new FileListAdapter(mp3FilesList, new FileListAdapter.FilesListViewHolder.CustomClickListener() {
            @Override
            public void onUserPlayIconClickListener(int position) {

            }
        });

        mRecyclerView.setAdapter(mFileListAdapter);
    }


    private void setSongInMediaPlayer(String currentPlayingSongPath) {
        try {
            mMediaPlayer.setDataSource(currentPlayingSongPath);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void restorePlayingState() {

        if (mPlayingState == 1) {
            mMediaPlayer.start();
            setPauseButtonImage();

        } else {
            //если вызвать паузу из остановленного состояния, то слетает и SetDataSource и prepare
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
            setPlayButtonImage();
            mPlayingState = 0;
        }

    }

    private void playButtonAction() {

        if (mPlayingState == 0) {
            mMediaPlayer.start();
            setPauseButtonImage();
            mPlayingState = 1;

        } else {

            mCurrentPlayingSongPosition = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.pause();
            setPlayButtonImage();
            mPlayingState = 0;
        }

    }

    private void setPlayButtonImage() {

        mPlay.setImageResource(R.drawable.play_circle_outline);

    }

    private void setPauseButtonImage() {

        mPlay.setImageResource(R.drawable.pause_circle_outline);

    }


    private void skipNext() {

        if (mSongsInPlaylist == 1) {
            return;
        } else if (mCurrentPlayingSongOrder == mSongsInPlaylist) {
            mCurrentPlayingSongOrder = 1;
        } else {
            mCurrentPlayingSongOrder += 1;
        }

        String filePath = loadSongByOrder(mCurrentPlayingSongOrder);
        mMediaPlayer.reset();
        setSongInMediaPlayer(filePath);
        restorePlayingState();


    }

    private void skipPrev() {

        if (mSongsInPlaylist == 1) {
            return;
        } else if (mCurrentPlayingSongOrder == 1) {
            mCurrentPlayingSongOrder = mSongsInPlaylist;
        } else {
            mCurrentPlayingSongOrder -= 1;
        }

        String filePath = loadSongByOrder(mCurrentPlayingSongOrder);
        mMediaPlayer.reset();
        setSongInMediaPlayer(filePath);
        restorePlayingState();

    }


    private String loadSongByOrder(int order) {

        Mp3File file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                .where(Mp3FileDao.Properties.Order.eq(order))
                .build()
                .unique();

        return file.getFullPath();

    }


    private List<Mp3File> loadFromDb() {

        List<Mp3File> list = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                .orderAsc(Mp3FileDao.Properties.Order)
                .build()
                .list();

        return list;

    }



    @Override
    public void onPrepared(MediaPlayer mp) {
        DataManager.writeLog(TAG, "onPrepared");

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        DataManager.writeLog(TAG, "onCompletion");
        skipNext();
    }

    private void releaseMP() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void saveToDb() {

        Mp3FileDao mMp3FileDao;
        mMp3FileDao = MusicApplication.getDaoSession().getMp3FileDao();
        mMp3FileDao.deleteAll();

        mMp3FileDao.insertOrReplaceInTx(mMp3FilesList);

    }

    class MyTaskForSavingToDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            saveToDb();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mMediaPlayer.reset();
            mPlayingState = 0;
            setPlayButtonImage();
            showSongsList(mMp3FilesList);
            mCurrentPlayingSongOrder = 1;
            mCurrentPlayingSongPath = loadSongByOrder(mCurrentPlayingSongOrder);
            setSongInMediaPlayer(mCurrentPlayingSongPath);

        }

    }
}
