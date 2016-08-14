package com.anray.musicapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.anray.musicapp.MusicApplication;
import com.anray.musicapp.R;
import com.anray.musicapp.data.storage.models.Mp3File;
import com.anray.musicapp.managers.DataManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "MainActivity";

    //private List<String> mSongsList;

    private Button mButton;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.choose_btn);
        mButton.setOnClickListener(this);

        mImageView = (ImageView) findViewById(R.id.image_iv);

        //mSongsList = new ArrayList<>();

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
                        List<Mp3File> mp3Base = new ArrayList<>();
                        File[] files = chosenFile.listFiles();
                        String artist;
                        String title;

                        for (int i = 0; i < files.length; i++) {
                            //mSongsList.add(files[i].toString());



                            DataManager.writeLog(TAG, files[i]);


                            mmr.setDataSource(files[i].toString());
                            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                            //setBackgroundFromByte(mmr, mImageView);
                            mp3Base.add(new Mp3File(files[i].toString(), i, title, artist));
                        }
                        mmr.release();
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

        showFileChooser();

    }

//    private void getId3Tags(File pathTofile, int i) {
//
//
//        try {
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            mediaPlayer.setDataSource(pathTofile.toString());
//
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (Exception e) {
//
//        }
//
//
//    }

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

}
