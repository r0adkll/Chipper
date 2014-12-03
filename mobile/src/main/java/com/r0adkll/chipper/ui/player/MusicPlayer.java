package com.r0adkll.chipper.ui.player;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Vote;
import com.r0adkll.chipper.playback.events.MediaSessionEvent;
import com.r0adkll.chipper.playback.events.PlayQueueEvent;
import com.r0adkll.chipper.playback.model.SessionState;
import com.r0adkll.chipper.ui.model.BaseFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/30/14.
 */
public class MusicPlayer extends BaseFragment implements MusicPlayerView {

    /**
     * ********************************************************************************************
     * <p/>
     * Variables
     */

    private final DecimalFormat minFormat = new DecimalFormat("#0");
    private final DecimalFormat secFormat = new DecimalFormat("00");

    @Inject
    MusicPlayerPresenter presenter;

    @Inject
    Bus mBus;

    @InjectView(R.id.buffer_bar)                ProgressBar mBufferBar;
    @InjectView(R.id.title)                     TextView mTitle;
    @InjectView(R.id.description)               TextView mDescription;
    @InjectView(R.id.previous)                  ImageView mPrevious;
    @InjectView(R.id.play_pause)                ImageView mPlayPause;
    @InjectView(R.id.next)                      ImageView mNext;
    @InjectView(R.id.shuffle)                   ImageView mShuffle;
    @InjectView(R.id.repeat)                    ImageView mRepeat;
    @InjectView(R.id.upvote)                    ImageView mUpvote;
    @InjectView(R.id.downvote)                  ImageView mDownvote;
    @InjectView(R.id.favorite)                  ImageView mFavorite;
    @InjectView(R.id.add)                       ImageView mAdd;
    @InjectView(R.id.player_content_pager)      ViewPager mPlayerContentPager;
    @InjectView(R.id.time_progress)             TextView mTimeProgress;
    @InjectView(R.id.scrubber)                  SeekBar mScrubber;
    @InjectView(R.id.time_total)                TextView mTimeTotal;
    @InjectView(R.id.master_previous)           ImageView mMasterPrevious;
    @InjectView(R.id.master_play_pause)         ImageView mMasterPlayPause;
    @InjectView(R.id.master_next)               ImageView mMasterNext;

//    @InjectView(R.id.cast_button)
//    android.support.v7.app.MediaRouteButton mCastButton;

    private MediaSessionCompat mSession;
    private MediaControllerCompat mController;
    private SlidingUpPanelLayout mSlidingLayout;

    /**********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_music_player, container, false);
        ButterKnife.inject(this, layout);
        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    protected Object[] getModules() {
        return new Object[]{
                new MusicPlayerModule(this)
        };
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Setup the sliding layout that this player is contained in
     *
     * @param layout the sliding layout
     */
    public void setSlidingLayout(SlidingUpPanelLayout layout) {
        mSlidingLayout = layout;
    }

    /**
     * Called when a current action ImageView is clicked
     *
     * @param action        the image view clicked
     */
    @OnClick({R.id.previous,R.id.play_pause,R.id.next,R.id.shuffle,R.id.repeat,R.id.upvote,
            R.id.downvote,R.id.favorite,R.id.add,R.id.master_previous, R.id.master_play_pause,
            R.id.master_next})
    public void onCurrentAction(ImageView action){
        switch (action.getId()){
            case R.id.previous:
                presenter.previous();
                break;
            case R.id.play_pause:
                presenter.playPause();
                break;
            case R.id.next:
                presenter.next();
                break;
            case R.id.shuffle:
                presenter.shuffle();
                break;
            case R.id.repeat:
                presenter.repeat();
                break;
            case R.id.upvote:
                presenter.upvote();
                break;
            case R.id.downvote:
                presenter.downvote();
                break;
            case R.id.favorite:
                presenter.favorite();
                break;
            case R.id.add:
                presenter.add();
                break;
            case R.id.master_next:
                presenter.next();
                break;
            case R.id.master_play_pause:
                presenter.playPause();
                break;
            case R.id.master_previous:
                presenter.previous();
                break;
        }
    }

    /**
     * MediaController Callbacks for listening to events from the media session
     */
    private MediaControllerCompat.Callback mControllerCallbacks = new MediaControllerCompat.Callback() {
        @Override
        public void onSessionDestroyed() {
            // TODO: Reset views
            mSlidingLayout.hidePanel();
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            presenter.onSessionEvent(event, extras);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            presenter.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            presenter.onMetadataChanged(metadata);
        }
    };

    /***********************************************************************************************
     *
     * Otto Subscriptions
     *
     */

    @Subscribe
    public void answerMediaSessionEvent(MediaSessionEvent event){
        mSession = event.session;
        mController = new MediaControllerCompat(getActivity(), mSession);
        mController.registerCallback(mControllerCallbacks);
        Timber.i("Media Session Received, Initializing Controller callbacks");
    }

    @Subscribe
    public void answerPlayQueueEvent(PlayQueueEvent event){
        // Update UI accordingly

    }

    /***********************************************************************************************
     *
     * View Methods
     *
     */

    @Override
    public MediaControllerCompat getMediaController() {
        return mController;
    }

    @Override
    public void setPlaybackProgress(int progress, int total) {
        if(total == 0 && progress == 0){
            mBufferBar.setVisibility(View.VISIBLE);
            mBufferBar.setIndeterminate(true);

            mScrubber.setEnabled(true);
            mScrubber.setIndeterminate(true);
        }else if(total == -1 && progress == -1){
            mBufferBar.setVisibility(View.GONE);
            mScrubber.setEnabled(false);
        }else{
            mBufferBar.setVisibility(View.VISIBLE);
            mBufferBar.setIndeterminate(false);
            mBufferBar.setProgress(progress);
            mBufferBar.setMax(total);

            mScrubber.setEnabled(true);
            mScrubber.setIndeterminate(false);
            mScrubber.setProgress(progress);
            mScrubber.setMax(total);

            // Update play progress text fields
            int secProg = (progress / 1000);
            int minProg = secProg / 60;

            int secTotal = (total / 1000);
            int minTotal = secTotal / 60;

            mTimeProgress.setText(minFormat.format(minProg) + ":" + secFormat.format(secProg % 60));
            mTimeTotal.setText(minFormat.format(minTotal) + ":" + secFormat.format(secTotal % 60));
        }
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setArtist(String artist) {
        mDescription.setText(artist);
    }

    @Override
    public void setIsPlaying(boolean value) {
        if(value){
            mPlayPause.setImageResource(R.drawable.ic_action_pause);
            mMasterPlayPause.setImageResource(R.drawable.ic_action_pause);
        }else{
            mPlayPause.setImageResource(R.drawable.ic_action_play);
            mMasterPlayPause.setImageResource(R.drawable.ic_action_play);
        }
    }

    @Override
    public void setShuffle(boolean value) {
        if(value){
            mShuffle.setColorFilter(getResources().getColor(R.color.primary));
        }else{
            mShuffle.clearColorFilter();
        }
    }

    @Override
    public void setRepeat(int mode) {
        switch (mode){
            case SessionState.MODE_ONE:
                mRepeat.setImageResource(R.drawable.ic_action_repeat_one);
                mRepeat.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_IN);
                break;
            case SessionState.MODE_ALL:
                mRepeat.setImageResource(R.drawable.ic_action_repeat);
                mRepeat.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_IN);
                break;
            case SessionState.MODE_NONE:
                mRepeat.setImageResource(R.drawable.ic_action_repeat);
                mRepeat.clearColorFilter();
                break;
        }
    }

    @Override
    public void setRating(int rating) {
        int accentColor = getResources().getColor(R.color.primary);
        switch (rating){
            case Vote.NONE:
                mUpvote.clearColorFilter();
                mDownvote.clearColorFilter();
                break;
            case Vote.UP:
                mUpvote.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
                mDownvote.clearColorFilter();
                break;
            case Vote.DOWN:
                mUpvote.clearColorFilter();
                mDownvote.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    @Override
    public void setFavorited(boolean value) {
        if(value){
            mFavorite.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_IN);
        }else{
            mFavorite.clearColorFilter();
        }
    }

    @Override
    public void disableControls() {
        mPlayPause.setEnabled(false);
        mNext.setEnabled(false);
        mPrevious.setEnabled(false);
        mMasterPlayPause.setEnabled(false);
        mMasterNext.setEnabled(false);
        mMasterPrevious.setEnabled(false);
    }

    @Override
    public void enableControls() {
        mPlayPause.setEnabled(true);
        mNext.setEnabled(true);
        mPrevious.setEnabled(true);
        mMasterPlayPause.setEnabled(true);
        mMasterNext.setEnabled(true);
        mMasterPrevious.setEnabled(true);
    }
}
