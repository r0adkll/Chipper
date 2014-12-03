package com.r0adkll.chipper.ui.player;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.r0adkll.chipper.api.model.Vote;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.playback.MusicService;
import com.r0adkll.chipper.playback.model.SessionState;
import com.r0adkll.chipper.prefs.BooleanPreference;
import com.r0adkll.chipper.prefs.IntPreference;
import com.r0adkll.chipper.qualifiers.SessionRepeatPreference;

import static android.support.v4.media.session.PlaybackStateCompat.*;
import static android.support.v4.media.MediaMetadataCompat.*;

/**
 * Created by r0adkll on 12/1/14.
 */
public class MusicPlayerPresenterImpl implements MusicPlayerPresenter {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private MusicPlayerView mView;
    private PlaylistManager mPlaylistManager;
    private VoteManager mVoteManager;

    private IntPreference mRepeatPreference;
    private BooleanPreference mShufflePreference;

    /**
     * Constructor
     */
    public MusicPlayerPresenterImpl(MusicPlayerView view,
                                    PlaylistManager playlistManager,
                                    VoteManager voteManager,
                                    IntPreference repeatPreference,
                                    BooleanPreference shufflePreference){
        mView = view;
        mPlaylistManager = playlistManager;
        mVoteManager = voteManager;
        mRepeatPreference = repeatPreference;
        mShufflePreference = shufflePreference;
    }

    /***********************************************************************************************
     *
     * Presenter Methods
     *
     */

    @Override
    public void previous() {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){
            controller.getTransportControls().skipToPrevious();
        }
    }

    @Override
    public void playPause() {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){

        }
    }

    @Override
    public void next() {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){
            controller.getTransportControls().skipToNext();
        }
    }

    @Override
    public void shuffle() {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){

            boolean shuffle = toggleShuffle();
            Bundle params = new Bundle();
            params.putBoolean(MusicService.EXTRA_SHUFFLE, shuffle);
            controller.sendCommand(MusicService.COMMAND_SHUFFLE, params, null);
        }
    }

    @Override
    public void repeat() {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){

            int mode = cycleRepeatMode();
            Bundle params = new Bundle();
            params.putInt(MusicService.EXTRA_REPEAT, mode);
            controller.sendCommand(MusicService.COMMAND_REPEAT, params, null);
        }
    }

    @Override
    public void upvote() {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){

        }
    }

    @Override
    public void downvote() {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){

        }
    }

    @Override
    public void favorite() {

    }

    @Override
    public void add() {

    }

    @Override
    public void onSessionEvent(String event, Bundle extras) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        // Update UI accordingly
        int pstate = state.getState();
        switch (pstate){
            case STATE_BUFFERING:
                mView.setPlaybackProgress(0 ,0);
                mView.setIsPlaying(true);
                mView.disableControls();
                break;
            case STATE_PLAYING:
                mView.setIsPlaying(true);
                mView.enableControls();
                break;
            case STATE_PAUSED:
                mView.setIsPlaying(false);
                break;
            case STATE_STOPPED:
                // Hide the player and show the shuffle play button
                mView.setIsPlaying(false);
                mView.setPlaybackProgress(-1,-1);
                mView.disableControls();
                break;
        }
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {

        // Update UI based on metadata change
        String title = metadata.getString(METADATA_KEY_TITLE);
        String artist = metadata.getString(METADATA_KEY_ARTIST);

        mView.setTitle(title);
        mView.setArtist(artist);

        long duration = metadata.getLong(METADATA_KEY_DURATION);
        RatingCompat rating = metadata.getRating(METADATA_KEY_USER_RATING);

        int vote = !rating.isRated() ? Vote.NONE :
                rating.isThumbUp() ? Vote.UP : Vote.DOWN;
        mView.setRating(vote);

        boolean favorited = Boolean.parseBoolean(metadata.getString(MusicService.METADATA_KEY_FAVORITED));
        mView.setFavorited(favorited);

    }

    /**
     * Cycle the repeat mode
     *
     * @return
     */
    private int cycleRepeatMode(){
        int mode = mRepeatPreference.get();
        mode++;
        if(mode > 2) mode = SessionState.MODE_NONE;
        return mode;
    }

    /**
     * Toggle the current shuffle
     * @return
     */
    private boolean toggleShuffle(){
        return !mShufflePreference.get();
    }

}
