package com.r0adkll.chipper.ui.player;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.KeyEvent;

import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.playback.MusicService;
import com.r0adkll.chipper.playback.model.SessionState;

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

    /**
     * Constructor
     */
    public MusicPlayerPresenterImpl(MusicPlayerView view,
                                    PlaylistManager playlistManager,
                                    VoteManager voteManager){
        mView = view;
        mPlaylistManager = playlistManager;
        mVoteManager = voteManager;
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

            // TODO: Pull the current shuffle state and determine if we need
            // TODO: to toggle
            Bundle params = new Bundle();
            params.putBoolean(MusicService.EXTRA_SHUFFLE, true);
            controller.sendCommand(MusicService.ACTION_SHUFFLE, params, null);
        }
    }

    @Override
    public void repeat() {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){

            // TODO: Pull hte current repeat state, and determine which mode to switch to
            Bundle params = new Bundle();
            params.putInt(MusicService.EXTRA_REPEAT, SessionState.MODE_ALL);
            controller.sendCommand(MusicService.ACTION_REPEAT, params, null);
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
}
