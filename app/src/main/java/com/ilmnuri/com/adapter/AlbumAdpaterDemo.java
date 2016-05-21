package com.ilmnuri.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ilmnuri.com.PlayActivity;
import com.ilmnuri.com.R;
import com.ilmnuri.com.event.AudioEvent;
import com.ilmnuri.com.model.AlbumModel;
import com.ilmnuri.com.model.Audio;
import com.ilmnuri.com.utility.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlbumAdpaterDemo extends RecyclerView.Adapter<AlbumAdpaterDemo.ViewHolder> {

    private Context mContext;
    private AlbumModel mAlbumModel;
    private OnItemClickListener mOnItemClickListener;
    private List<ViewHolder> mViewHolders = new ArrayList<>();
    Handler handler;
    File dir;

    public AlbumAdpaterDemo(Context context, AlbumModel albumModel, OnItemClickListener listener) {
        mContext = context;
        mAlbumModel = albumModel;
        this.mOnItemClickListener = listener;
        dir = new File(context.getExternalFilesDir(null), "audio");
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdirs();
        }
        if (isDirectoryCreated) {
            // do something
            Log.d("mkdirs option", "Directory already exists.");
        }
    }

    public void deleteItem(int position) {
        mAlbumModel.getAudios().remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        mViewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Audio audio = getItem(position);

        if (holder.tvAlbumTitle != null) {
            assert audio != null;
            holder.tvAlbumTitle.setText(audio.getTrackName().replace(".mp3", "").replace("_", " "));
        }
        if (holder.audioSize != null) {
            assert audio != null;
            holder.audioSize.setText(audio.getTrackSize());
        }

        if (Utils.checkFileExist(dir.getPath() + "/" + mAlbumModel.getAudios().get(position).getTrackName())) {
            if (holder.btnDownload != null) {
                holder.btnDownload.setVisibility(View.GONE);
            }
            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
        } else {
            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.GONE);
            }
            if (holder.btnDownload != null) {
                holder.btnDownload.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mAlbumModel.getAudios().size();
    }

    private Audio getItem(int position) {
        if (position >= 0 && position < mAlbumModel.getAudios().size()) {
            return mAlbumModel.getAudios().get(position);
        }
        return null;
    }

    public void onEvent(AudioEvent event) {

        switch (event.getType()) {

            case STOP:
                for (ViewHolder vh : mViewHolders) {
                    if (vh != null) {
                        Audio audio = mAlbumModel.getAudios().get(vh.getAdapterPosition());
                        if (audio != null && audio.getTrackId() == (event.getAudio().getTrackId())) {
                            vh.closeSeekBar(true);
                        }
                    }
                }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.rl_item_album)
        LinearLayout mLinearLayout;

        @Nullable
        @Bind(R.id.tv_item_album)
        TextView tvAlbumTitle;

        @Nullable
        @Bind(R.id.btn_delete)
        ImageButton btnDelete;

        @Nullable
        @Bind(R.id.btn_download)
        ImageButton btnDownload;

        @Nullable
        @Bind(R.id.audioSize)
        TextView audioSize;

        @Nullable
        @Bind(R.id.progressBar)
        SeekBar mProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            try {
                ButterKnife.bind(this, itemView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler = new Handler();
        }

        @OnClick(R.id.rl_item_album)
        void clickItem() {
            Intent intent = new Intent(mContext, PlayActivity.class);
            intent.putExtra("category", mAlbumModel.getCategory());
            intent.putExtra("url", mAlbumModel.getCategory() + "/" + mAlbumModel.getAlbum() + "/" + mAlbumModel.getAudios().get(getAdapterPosition()).getTrackName());
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            mContext.startActivity(intent);
        }

        @OnClick({R.id.btn_download, R.id.btn_delete})
        void options(View view) {
            switch (view.getId()) {
                case R.id.btn_delete:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onDeleteListener(mAlbumModel, getAdapterPosition());
                    }
                    break;
                case R.id.btn_download:
                    mOnItemClickListener.onDownloadListener(mAlbumModel, getAdapterPosition());

                    break;
            }
        }


        public void closeSeekBar(boolean isSeekBar) {
            if (isSeekBar) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                btnDelete.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                notifyItemChanged(getAdapterPosition());

//                        Global.getInstance().setAudio(null);
//                        handler.removeCallbacks(mUpdateTimeTask);
            }
                });

            }
        }

//        private Runnable mUpdateTimeTask = new Runnable() {
//            public void run() {
//                final int current_position = Global.getInstance().getCurrent_position();
//
//                if (mProgressBar != null) {
//                    mProgressBar.setProgress(current_position);
//                }
//
//
//                handler.postDelayed(this, 100);
//            }
//        };
//        private void activeSeekBar() {
//
//            ((Activity) mContext).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (mProgressBar != null) {
//                        mProgressBar.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
//
//            removeCallback();
//            final int position = Global.getInstance().getCurrent_position();
//
//            if (mProgressBar != null) {
//                mProgressBar.setProgress(position);
//            }
//
//
//            updateProgress();
//        }
//
//        private void unActiveSeekBar() {
//
//            ((Activity) mContext).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (mProgressBar != null) {
//                        mProgressBar.setVisibility(View.GONE);
//                    }
//
//                }
//            });
//
//            removeCallback();
//        }
//
//        public void removeCallback() {
//            handler.removeCallbacks(mUpdateTimeTask);
//        }
//
//        public void updateProgress() {
//            handler.postDelayed(mUpdateTimeTask, 100);
//        }
//
    }

    public interface OnItemClickListener {
        void onDeleteListener(AlbumModel model, int position);

        void onDownloadListener(AlbumModel model, int position);
    }
}
