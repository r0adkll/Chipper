package com.r0adkll.chipper.tv.ui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by r0adkll on 12/8/14.
 */
public class PlaylistPresenter extends Presenter {

    private static Context mContext;
    private static int CARD_WIDTH = 313;
    private static int CARD_HEIGHT = 176;
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("M/d/yy 'at' HH:mm a");

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        ImageCardView cardView = new ImageCardView(mContext);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(mContext.getResources().getColor(R.color.fastlane_background));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ViewHolder holder = (ViewHolder) viewHolder;
        Playlist playlist = (Playlist) item;
        holder.setPlaylist(playlist);

        // Apply content
        holder.applyDefaultCardImage();
        holder.getCardView().setTitleText(playlist.name);
        holder.getCardView().setContentText(String.format("Updated at %s", mDateFormat.format(new Date(playlist.updated*1000))));
        holder.getCardView().setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        holder.getCardView().setMainImageScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.getCardView().setBadgeImage(mContext.getResources().getDrawable(R.drawable.ic_action_queue_music));
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }


    /**
     * Chiptune Presenter ViewHolder
     */
    static class ViewHolder extends Presenter.ViewHolder {

        private Playlist mPlaylist;
        private ImageCardView mCardView;
        private Drawable mDefaultCardImage;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
            mDefaultCardImage = mContext.getResources().getDrawable(R.drawable.chipper_round_watch_bg);
        }

        public void setPlaylist(Playlist playlist){
            mPlaylist = playlist;
        }

        public Playlist getPlaylist(){
            return mPlaylist;
        }

        public ImageCardView getCardView() {
            return mCardView;
        }

        public void applyDefaultCardImage(){
            mCardView.setMainImage(mDefaultCardImage);
        }

    }

}
