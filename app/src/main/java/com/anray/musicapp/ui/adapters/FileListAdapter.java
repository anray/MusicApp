package com.anray.musicapp.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anray.musicapp.R;
import com.anray.musicapp.data.storage.models.Mp3File;
import com.anray.musicapp.managers.DataManager;

import java.util.List;

/**
 * Created by anray on 14.08.2016.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FilesListViewHolder> {

    private static final String TAG = "UsersAdapter";

    private List<Mp3File> mMp3Files;
    private Context mContext;
    private FilesListViewHolder.CustomClickListener mCustomClickListener;


    public FileListAdapter(List<Mp3File> files, FilesListViewHolder.CustomClickListener customClickListener) {
        mMp3Files = files;
        this.mCustomClickListener = customClickListener;
    }

    @Override
    public FilesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);


        return new FilesListViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(final FilesListViewHolder holder, int position) {


        final Mp3File file = mMp3Files.get(position);


        holder.mArtist.setText(file.getArtist());

        //if title is empty
        if (file.getTitle() != null && !file.getTitle().isEmpty()) {
            holder.mTitle.setText(file.getTitle());
        } else {
            String line = file.getFullPath();
            //get fileName
            line = line.substring(line.lastIndexOf("/") + 1, line.indexOf(".") - 1);
            holder.mTitle.setText(line);

        }

        //setting of picture
        if (file.getPlayingFlag() == 1) {
            holder.mPlay.setImageResource(R.drawable.pause_circle_outline);
        } else {
            holder.mPlay.setImageResource(R.drawable.play_circle_outline);
        }

        DataManager.writeLog(TAG, file.getFullPath());
        setBackgroundFromByte(file.getFullPath(), holder.mAlbumCover);


        //setting highlighting to current playing item
        if (file.getPlayingFlag() == 1) {
            holder.mRelativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
            holder.mPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause_circle_outline));
        } else {
            holder.mRelativeLayout.setBackgroundColor(0);
            holder.mPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_circle_outline));
        }



    }

    /**
     * Sets background to album cover if it is present, if it is not - then doing notihing
     *
     * @param imageView
     */
    private void setBackgroundFromByte(String path, ImageView imageView) {

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        mmr.setDataSource(path);

        try {
            byte[] image = mmr.getEmbeddedPicture();
            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                imageView.setBackground(new BitmapDrawable(bitmap));
            } else {

                imageView.setBackground(mContext.getResources().getDrawable(R.drawable.image_not_available));
            }
        } catch (Exception e) {
            imageView.setBackground(mContext.getResources().getDrawable(R.drawable.image_not_available));
        }

        mmr.release();

    }

    @Override
    public int getItemCount() {
        return mMp3Files.size();
    }

    public static class FilesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mTitle, mArtist;
        protected ImageView mAlbumCover, mPlay;
        protected RelativeLayout mRelativeLayout;

        private CustomClickListener mListener;


        public FilesListViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            this.mListener = customClickListener;


            mRelativeLayout = (RelativeLayout) itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_tv);
            mArtist = (TextView) itemView.findViewById(R.id.artist_tv);

            mAlbumCover = (ImageView) itemView.findViewById(R.id.album_thumb);
            mPlay = (ImageView) itemView.findViewById(R.id.play_icon_iv);

            mRelativeLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
//                switch (v.getId()) {
//                    case R.id.play_icon_iv:
                        mListener.onUserPlayIconClickListener(getAdapterPosition());
                        DataManager.writeLog("Clicked");
//                        break;
//                }


            }

        }


        public interface CustomClickListener {

            void onUserPlayIconClickListener(int position);

        }
    }


    //region для свайпов и драгов карточек

//
//    @Override
//    public void onItemMove(int fromPosition, int toPosition) {
//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i < toPosition; i++) {
//                Collections.swap(mUsers, i, i + 1);
//            }
//        } else {
//            for (int i = fromPosition; i > toPosition; i--) {
//                Collections.swap(mUsers, i, i - 1);
//            }
//        }
//        notifyItemMoved(fromPosition, toPosition);
//        //return true;
//    }
//
//    @Override
//    public void onItemDismiss(int position) {
//        mUsers.remove(position);
//        notifyItemRemoved(position);
//    }
//
//    //endregion
}

