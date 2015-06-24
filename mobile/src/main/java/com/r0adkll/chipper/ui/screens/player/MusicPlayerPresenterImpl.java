package com.r0adkll.chipper.ui.screens.player;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.Vote;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.playback.MusicService;
import com.r0adkll.chipper.playback.events.PlayProgressEvent;
import com.r0adkll.chipper.playback.events.PlayQueueEvent;
import com.r0adkll.chipper.playback.model.PlayQueue;
import com.r0adkll.chipper.playback.model.SessionState;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.utils.prefs.IntPreference;
import com.r0adkll.chipper.utils.CallbackHandler;

import timber.log.Timber;

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
    private PlayQueue mQueue;
    private SessionState mState;

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
            int state = controller.getPlaybackState().getState();
            if(state == PlaybackStateCompat.STATE_PLAYING){
                controller.getTransportControls().pause();
            }else if(state == PlaybackStateCompat.STATE_PAUSED){
                controller.getTransportControls().play();
            }
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
    public void seek(long position) {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){
            controller.getTransportControls().seekTo(position);
        }
    }

    @Override
    public void upvote() {
        if(mQueue != null && mState != null){
            final Chiptune current = mQueue.current(mState);

            mVoteManager.upvote(current, new CallbackHandler() {
                @Override
                public void onHandle(Object value) {
                    mView.setRating(Vote.UP);
                }

                @Override
                public void onFailure(String msg) {
                    // TODO: Display error to UI
                    Timber.e("Unable to upvote %s", current.title);
                    mView.showSnackBar(msg);
                }
            });

        }
    }

    @Override
    public void downvote() {
        if(mQueue != null && mState != null){
            final Chiptune current = mQueue.current(mState);

            mVoteManager.downvote(current, new CallbackHandler() {
                @Override
                public void onHandle(Object value) {
                    mView.setRating(Vote.DOWN);
                }

                @Override
                public void onFailure(String msg) {
                    // TODO: Display error to UI
                    Timber.e("Unable to downvote %s : %s", current.title, msg);
                    mView.showSnackBar(msg);
                }
            });
        }
    }

    @Override
    public void favorite() {
        if(mQueue != null && mState != null){
            Chiptune current = mQueue.current(mState);

            if(!mPlaylistManager.isFavorited(current)) {
                mPlaylistManager.addToFavorites(current);
                mView.setFavorited(true);
            }else{
                mPlaylistManager.getFavorites().remove(current);
                mView.setFavorited(false);
            }
        }
    }

    @Override
    public void add() {
        if(mQueue != null && mState != null){
            final Chiptune current = mQueue.current(mState);
            mPlaylistManager.addToPlaylist(mView.getActivity(), new CallbackHandler<Playlist>() {
                @Override
                public void onHandle(Playlist value) {
                    // Chiptune added to playlist, TODO: notify UI
                    mView.showSnackBar("%s added to %s", current.title, value.name);
                }

                @Override
                public void onFailure(String msg) {
                    // Chiptune failed to be added to playlist, TODO: notify UI
                    if(msg != null){
                        mView.showSnackBar(msg);
                    }
                }
            }, current);
        }
    }

    @Override
    public void onQueueItemSelected(Chiptune chiptune) {
        MediaControllerCompat controller = mView.getMediaController();
        if(controller != null){
            Bundle xtras = new Bundle();
            xtras.putString(MusicService.EXTRA_CHIPTUNE, chiptune.id);
            controller.sendCommand(MusicService.COMMAND_QUEUE_JUMP, xtras, null);
        }
    }

    @Override
    public void onPlayProgressEvent(PlayProgressEvent event) {
        mView.setPlaybackProgress(event.position, event.duration);
    }

    @Override
    public void onPlayQueueEvent(PlayQueueEvent event) {
        mQueue = event.queue;
        mState = event.state;

        // Update accordingly
        mView.setShuffle(mState.isShuffleEnabled());
        mView.setRepeat(mState.getRepeatMode());

        // set the queue list
        mView.setQueueList(mQueue.getDisplayList(mState));
    }

    @Override
    public void onSessionDestroyed() {
        mQueue = null;
        mState = null;
    }

    @Override
    public void onSessionEvent(String event, Bundle extras) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        if(state != null) {
            // Update UI accordingly
            int pstate = state.getState();
            switch (pstate) {
                case STATE_BUFFERING:
                    mView.setPlaybackProgress(0, 0);
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
                    mView.setPlaybackProgress(-1, -1);
                    mView.disableControls();
                    break;
            }
        }
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {
        if(metadata != null) {

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
