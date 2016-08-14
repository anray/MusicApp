package com.anray.musicapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anray.musicapp.R;
import com.anray.musicapp.data.storage.models.Mp3File;

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

        //mDataManager = DataManager.getInstance();
        final Mp3File file = mMp3Files.get(position);


        holder.mArtist.setText(file.getArtist());

        //if title is empty
        if (file.getTitle() != null && !file.getTitle().isEmpty()){
            holder.mTitle.setText(file.getTitle());
        } else {
            String line = file.getFullPath();
            line = line.substring(line.lastIndexOf("/")+1,line.indexOf(".")-1);
            holder.mTitle.setText(line);

        }


        //закрашивает сердце у тех юзеров, которых я лайкнул
        //и снимает закраску у тех, кого анлайкнул
//        for (Likes like : file.getMLikes()) {
//            if (like.getUserIdWhoLiked().equalsIgnoreCase(mDataManager.getPreferencesManager().getUserId())) {
//                holder.mLikesHeart.setImageResource(R.drawable.heart);
//                //break потому что не надо дальше проходить лист лайков, потому что у нас в разметке дефолтное значение - незакрашенный лайк
//                break;
//            } else {
//                holder.mLikesHeart.setImageResource(R.drawable.heart_outline);
//            }
//        }


    }

    @Override
    public int getItemCount() {
        return mMp3Files.size();
    }

    public static class FilesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mTitle, mArtist;
        protected ImageView mAlbumCover, mPlay;

        private CustomClickListener mListener;


        public FilesListViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            this.mListener = customClickListener;


            mTitle = (TextView) itemView.findViewById(R.id.title_tv);
            mArtist = (TextView) itemView.findViewById(R.id.artist_tv);


            mAlbumCover = (ImageView) itemView.findViewById(R.id.album_thumb);
            mPlay = (ImageView) itemView.findViewById(R.id.play_icon_iv);

            //mDummy = mUserImage.getContext().getResources().getDrawable(R.drawable.user_bg);

            mPlay.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                switch (v.getId()) {
                    case R.id.play_icon_iv:
                        mListener.onUserPlayIconClickListener(getAdapterPosition());
                        break;

                }

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

