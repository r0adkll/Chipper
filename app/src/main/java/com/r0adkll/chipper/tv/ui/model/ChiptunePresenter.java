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

/**
 * Created by r0adkll on 12/8/14.
 */
public class ChiptunePresenter extends Presenter {

    private static Context mContext;
    private static int CARD_WIDTH = 313;
    private static int CARD_HEIGHT = 176;

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
        Chiptune chiptune = (Chiptune) item;
        holder.setChiptune(chiptune);

        // Apply content
        holder.applyDefaultCardImage();
        holder.getCardView().setTitleText(chiptune.title);
        holder.getCardView().setContentText(chiptune.artist);
        holder.getCardView().setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        holder.getCardView().setMainImageScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }


    /**
     * Chiptune Presenter ViewHolder
     */
    static class ViewHolder extends Presenter.ViewHolder {

        private Chiptune mChiptune;
        private ImageCardView mCardView;
        private Drawable mDefaultCardImage;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
            mDefaultCardImage = mContext.getResources().getDrawable(R.drawable.chipper_round_watch_bg);
        }

        public void setChiptune(Chiptune chiptune){
            mChiptune = chiptune;
        }

        public Chiptune getChiptune(){
            return mChiptune;
        }

        public ImageCardView getCardView() {
            return mCardView;
        }

        public void applyDefaultCardImage(){
            mCardView.setMainImage(mDefaultCardImage);
        }

    }

}
