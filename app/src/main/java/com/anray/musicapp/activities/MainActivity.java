package com.anray.musicapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "MainActivity";

    //private List<String> mSongsList;

    private Button mButton;
    private ImageView mMainCoverImage, mPlay, mNext, mPrevious;

    private List<Mp3File> mp3Base;

    private RecyclerView mRecyclerView;
    private FileListAdapter mFileListAdapter;

    private MediaPlayer mMediaPlayer;

    private int mListItemsCount;

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

        if (mFileListAdapter == null) {
            mFileListAdapter = new FileListAdapter(loadFromDb(), new FileListAdapter.FilesListViewHolder.CustomClickListener() {
                @Override
                public void onUserPlayIconClickListener(int position) {

                }
            });
        }
        mRecyclerView.swapAdapter(mFileListAdapter, false);


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
    protected void onPause() {
        super.onPause();
        DataManager.writeLog(TAG, "onPause");
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg4-generic");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
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
                        mp3Base = new ArrayList<>();
                        File[] files = chosenFile.listFiles();
                        String artist;
                        String title;

                        for (int i = 0; i < files.length; i++) {
                            //mSongsList.add(files[i].toString());


                            DataManager.writeLog(TAG, files[i]);


                            mmr.setDataSource(files[i].toString());
                            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                            //setBackgroundFromByte(mmr, mMainCoverImage);
                            mp3Base.add(new Mp3File(files[i].toString(), i + 1, title, artist));

                        }
                        mmr.release();
                        new MyTaskForSavingToDb().execute();
                        new MyTaskForLoadingFromDb().execute();

                    }

                } else {
                    DataManager.showToast("The Storage is not ready!");
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_btn:
                showFileChooser();
                break;
            case R.id.play_iv:

                if (mMediaPlayer == null) {

                    Mp3File file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                            .where(Mp3FileDao.Properties.PlayingFlag.eq(1))
                            .build()
                            .unique();

                    if (file != null) {
                        startPlaying(file.getFullPath());
                    } else {
                        firstStart();
                    }
                } else {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mPlay.setImageResource(R.drawable.play_circle_outline);
                    } else if (!mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                        mPlay.setImageResource(R.drawable.pause_circle_outline);
                    }
                }
//                if (mMediaPlayer.isPlaying()) {
//                    mMediaPlayer.pause();
//                    mPlay.setImageResource(R.drawable.play_circle_outline);
//                } else {
//                    mMediaPlayer.start();
//                    mPlay.setImageResource(R.drawable.pause_circle_outline);
//                }
//        }catch(Exception e){
//            firstStart();
//            mPlay.setImageResource(R.drawable.pause_circle_outline);
//        }
                break;
            case R.id.next_iv:
                skipNext();
                break;
            case R.id.previous_iv:
                skipPrevious();
                break;
        }


    }


    private void firstStart() {
        Mp3File file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                .where(Mp3FileDao.Properties.Order.eq(1))
                .build()
                .unique();

        //if files are not loaded from directory, at all
        if (file != null) {

            mMediaPlayer = new MediaPlayer();
            startPlaying(file.getFullPath());

            //set current playing flag
            file.setPlayingFlag(1);
            MusicApplication.getDaoSession().getMp3FileDao().insertOrReplace(file);
        }
    }

    private Mp3File getMusicFileFromDB(String jkhkj) {


        Mp3File file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                .where(Mp3FileDao.Properties.PlayingFlag.eq(1))
                .build()
                .unique();

        if (file != null) {
            return MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                    .where(Mp3FileDao.Properties.Order.eq(file.getOrder() + 1))
                    .build()
                    .unique();
        } else {

        }
        return file;
    }


    private void skipNext() {

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            try {
                //is there any track?
                mMediaPlayer.getTrackInfo();

                stopPlaying();

                //what was playing
                Mp3File file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                        .where(Mp3FileDao.Properties.PlayingFlag.eq(1))
                        .build()
                        .unique();


                //count next
                int order;
                if (file.getOrder() == (int) MusicApplication.getDaoSession().queryBuilder(Mp3File.class).count()) {
                    order = 1;
                } else {
                    order = file.getOrder() + 1;
                }

                //remove current playing flag
                file.setPlayingFlag(0);
                MusicApplication.getDaoSession().getMp3FileDao().insertOrReplace(file);

                //set current playing flag
                file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                        .where(Mp3FileDao.Properties.Order.eq(order))
                        .build()
                        .unique();

                file.setPlayingFlag(1);
                MusicApplication.getDaoSession().getMp3FileDao().insertOrReplace(file);

                mMediaPlayer.reset();
                startPlaying(file.getFullPath());


            } catch (Exception e) {

            }
        }
    }

    private void skipPrevious() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            try {
                //is there any track?
                mMediaPlayer.getTrackInfo();

                stopPlaying();

                //what was playing
                Mp3File file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                        .where(Mp3FileDao.Properties.PlayingFlag.eq(1))
                        .build()
                        .unique();


                //count next
                int order;
                if (file.getOrder() == 1) {
                    order = (int) MusicApplication.getDaoSession().queryBuilder(Mp3File.class).count();
                } else {
                    order = file.getOrder() - 1;
                }

                //remove current playing flag
                file.setPlayingFlag(0);
                MusicApplication.getDaoSession().getMp3FileDao().insertOrReplace(file);

                //set current playing flag
                file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                        .where(Mp3FileDao.Properties.Order.eq(order))
                        .build()
                        .unique();

                file.setPlayingFlag(1);
                MusicApplication.getDaoSession().getMp3FileDao().insertOrReplace(file);

                mMediaPlayer.reset();
                startPlaying(file.getFullPath());

            } catch (Exception e) {

            }
        }
    }

    private void startPlaying(String pathTofile) {

        if (mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }

        try {

            mMediaPlayer.setDataSource(pathTofile.toString());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mPlay.setImageResource(R.drawable.pause_circle_outline);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying() {
        mMediaPlayer.stop();
        mPlay.setImageResource(R.drawable.play_circle_outline);
    }

    /**
     * Sets background to album cover if it is present, if it is not - then doing notihing
     *
     * @param mmr
     * @param imageView
     */
    private void setBackgroundFromByte(MediaMetadataRetriever mmr, ImageView imageView) {

        byte[] image = mmr.getEmbeddedPicture();
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            imageView.setBackground(new BitmapDrawable(bitmap));
        }

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

        }

    }

    class MyTaskForLoadingFromDb extends AsyncTask<Void, Void, List<Mp3File>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Mp3File> doInBackground(Void... params) {


            return loadFromDb();
        }

        @Override
        protected void onPostExecute(List<Mp3File> result) {
            super.onPostExecute(result);

            if (mMediaPlayer != null){
                mMediaPlayer.release();
                mMediaPlayer = null;
                mPlay.setImageResource(R.drawable.play_circle_outline);
            }

            mFileListAdapter = new FileListAdapter(result, new FileListAdapter.FilesListViewHolder.CustomClickListener() {
                @Override
                public void onUserPlayIconClickListener(int position) {

                }
            });

            mRecyclerView.setAdapter(mFileListAdapter);

        }

    }

    private void saveToDb() {


        Mp3FileDao mMp3FileDao;

        mMp3FileDao = MusicApplication.getDaoSession().getMp3FileDao();

        mMp3FileDao.deleteAll();


        mMp3FileDao.insertOrReplaceInTx(mp3Base);


    }


    private List<Mp3File> loadFromDb() {

        List<Mp3File> list = new ArrayList<>();

        list = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
                .orderAsc(Mp3FileDao.Properties.Order)
                .build()
                .list();

        //need to current number of tracks
        mListItemsCount = list.size();



        return list;


    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        DataManager.writeLog(TAG, "onPrepared");
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        DataManager.writeLog(TAG, "onCompletion");

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

//        //what was playing
//        Mp3File file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
//                .where(Mp3FileDao.Properties.PlayingFlag.eq(1))
//                .build()
//                .unique();
//
//        //remove current playing flag
//        if (file != null) {
//            file.setPlayingFlag(0);
//            MusicApplication.getDaoSession().getMp3FileDao().insertOrReplace(file);
//        }
    }


//    private void play(String pathTofile) {
//        try {
//
//            //remove flag from now playing
//            Mp3File file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
//                    .where(Mp3FileDao.Properties.PlayingFlag.eq(1))
//                    .build()
//                    .unique();
//
//            if (file != null) {
//                file.setPlayingFlag(0);
//            } else {
//                //verification if nothing is playing
//                file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
//                        .where(Mp3FileDao.Properties.Order.eq(1))
//                        .build()
//                        .unique();
//
//                startPlaying(file.getFullPath());
//
//            }
//
//            MusicApplication.getDaoSession().getMp3FileDao().insertOrReplace(file);
//            //remove flag from now playing
//
//
//            startPlaying(pathTofile.toString());
//
//
//            //add flag to now playing
//            file = MusicApplication.getDaoSession().queryBuilder(Mp3File.class)
//                    .where(Mp3FileDao.Properties.FullPath.eq(pathTofile.toString()))
//                    .build()
//                    .unique();
//
//            file.setPlayingFlag(1);
//
//            MusicApplication.getDaoSession().getMp3FileDao().insertOrReplace(file);
//            //add flag to now playing
//
//            //redraw fileList
//            mFileListAdapter.notifyDataSetChanged();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
