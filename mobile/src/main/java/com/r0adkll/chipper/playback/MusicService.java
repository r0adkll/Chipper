package com.r0adkll.chipper.playback;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.playback.model.AudioSession;
import com.r0adkll.chipper.playback.model.PlaybackState;
import com.r0adkll.chipper.playback.model.SessionState;
import com.r0adkll.chipper.prefs.BooleanPreference;
import com.r0adkll.chipper.prefs.IntPreference;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by r0adkll on 11/25/14.
 */
public class MusicService extends Service {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final String MEDIA_SESSION_TAG = "Chipper Session";

    public static final String INTENT_ACTION_PLAY = "com.r0adkll.chipper.intent.PLAY";
    public static final String INTENT_ACTION_PAUSE = "com.r0adkll.chipper.intent.PAUSE";
    public static final String INTENT_ACTION_PLAYPAUSE = "com.r0adkll.chipper.intent.PLAYPAUSE";
    public static final String INTENT_ACTION_SHUFFLEPLAY = "com.r0adkll.chipper.action.WIDGET_SHUFFLE_PLAY";
    public static final String INTENT_ACTION_NEXT = "com.r0adkll.chipper.intent.NEXT";
    public static final String INTENT_ACTION_PREV = "com.r0adkll.chipper.intent.PREV";
    public static final String INTENT_ACTION_EXIT = "com.r0adkll.chipper.intent.EXIT";
    public static final String INTENT_ACTION_UPVOTE = "com.r0adkll.chipper.intent.UPVOTE";
    public static final String INTENT_ACTION_DOWNVOTE = "com.r0adkll.chipper.intent.DOWNVOTE";


    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject AudioManager mAudioManager;
    @Inject NotificationManagerCompat mNotificationManager;
    @Inject PlaylistManager mPlaylistManager;
    @Inject VoteManager mVoteManager;
    @Inject ChiptuneProvider mProvider;
    @Inject CashMachine mATM;
    @Inject Bus mBus;

    @Inject BooleanPreference mShufflePref;
    @Inject IntPreference mRepeatPref;
    @Inject AudioPlayer mPlayer;

    private SessionState mCurrentState;
    private MediaSessionCompat mCurrentSession;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    public void onCreate() {
        super.onCreate();
        ChipperApp.get(this).inject(this);

        // Register for the Otto bus
        mBus.register(this);

        // Setup the Player Callbacks
        mPlayer.setPlayerCallbacks(mPlayerCallbacks);

        // Register Receivers
        registerReceiver(mNoisyAudioStreamReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        // Initialize the Session State
        mCurrentState = new SessionState(mShufflePref, mRepeatPref);

        // Initialize the Media Session
        mCurrentSession = new MediaSessionCompat(this, MEDIA_SESSION_TAG);
        mCurrentSession.setCallback(mMediaSessionCallbacks);
        mCurrentSession.setActive(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: Handle incoming intent

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Un-register from the otto bus
        mBus.unregister(this);

        // Unregister Receivers
        unregisterReceiver(mNoisyAudioStreamReceiver);

        // Relinquish playback resources
        stop();

        // Abandon Focus
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);

        // Release the Session
        if(mCurrentSession != null){
            mCurrentSession.release();
        }

    }

    /***********************************************************************************************
     *
     * Playback Methods
     *
     */

    private void play(){
        // Start playback
        mPlayer.play();
        mCurrentSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_PLAYING));

        // Update Notification

        // Dispatch Otto Event

    }

    private void pause(){
        // Pause Playback
        mPlayer.pause();
        mCurrentSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_PAUSED));

        // Update Notification


        // Dispatch Otto event

    }

    private void playPause(){
        if(mCurrentState.isValid()){
            if(mPlayer.isPlaying()){
                pause();
            }else{
                play();
            }
        }else{
            // Play a Random Chiptune
            coldStartRandomPlayback();
        }
    }

    private void stop(){
        mPlayer.stop();
        mPlayer.release();
        mCurrentSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_STOPPED));

        // Update Notification

        // Dispatch Otto Event

    }

    private void next(){

    }

    private void previous(){

    }

    private void shuffle(boolean enabled){
        mCurrentState.setShuffle(enabled);
    }

    private void repeat(int mode){
        mCurrentState.setRepeatMode(mode);
    }

    private void seek(long position){
        mPlayer.seekTo((int) position);
    }


    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Starting playing a chiptune (and by association it's playlist)
     *
     * @param chiptune      the chiptune to play
     * @param playlist      the associated playlist
     */
    private void playChiptune(Chiptune chiptune, Playlist playlist){

        // Request audio focus and only play if granted
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){

            // Check the current player state, reset if needed
            if(mPlayer.getCurrentState() == AudioPlayer.COMPLETED ||
                    mPlayer.getCurrentState() == AudioPlayer.PAUSED ||
                    mPlayer.isPlaying()){
                stop();
            }

            // Update media session state to buffering
            mCurrentSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_BUFFERING));

            // Update the current state
            mCurrentState.updateState(chiptune, playlist);

            // Log
            Timber.i("Preparing[%s] at [%s]", chiptune.title, chiptune.stream_url);

            // Build audio session to play
            AudioSession session = new AudioSession(mATM, chiptune);
            mPlayer.prepareAudioSession(session, new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    // Initiate Playback
                    play();

                    // TODO: Update Notification

                    // TODO: Update Widget


                }
            }, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    // Update Player State
                    mPlayer.setCurrentState(AudioPlayer.COMPLETED);

                    // Attempt to get the next chiptune and play it

                }
            });

        }


    }

    /**
     * Build the MediaSession playback state after a state change in the service
     *
     * @param playBackState     the state to update with
     * @return                  the state
     */
    private PlaybackStateCompat buildPlaybackState(int playBackState){

        // Determine appropriate playback rate
        int playbackRate = 0;
        if(playBackState == PlaybackStateCompat.STATE_PLAYING) {
            playbackRate = 1;
        }

        // Build the playback state
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setState(playBackState, mPlayer.getCurrentPosition(), playbackRate);

        // Set the Playback available actions
        stateBuilder.setActions(
            PlaybackStateCompat.ACTION_PLAY |
            PlaybackStateCompat.ACTION_PAUSE |
            PlaybackStateCompat.ACTION_PLAY_PAUSE |
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
            PlaybackStateCompat.ACTION_SEEK_TO |
            PlaybackStateCompat.ACTION_SET_RATING |
            PlaybackStateCompat.ACTION_STOP
        );

        return stateBuilder.build();
    }

    /**
     * Build the metadata related to the current chiptune
     *
     * @return  the media metadata attributed to the current song
     */
    private MediaMetadataCompat buildMetaData(){
        Chiptune chiptune = mCurrentState.getCurrentChiptune();

        // Create the builder
        MediaMetadataCompat.Builder metaBuilder = new MediaMetadataCompat.Builder();

        if(chiptune != null){

            int voteValue = mVoteManager.getUserVoteValue(mCurrentState.getCurrentChiptune().id);
            RatingCompat rating;
            if(voteValue != 0) {
                rating = RatingCompat.newThumbRating(voteValue == 1 ? true : false);
            }else{
                rating = RatingCompat.newUnratedRating(RatingCompat.RATING_THUMB_UP_DOWN);
            }

            metaBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, chiptune.artist)
                       .putString(MediaMetadataCompat.METADATA_KEY_TITLE, chiptune.title)
                       .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, chiptune.length)
                       .putRating(MediaMetadataCompat.METADATA_KEY_RATING, rating);

        }

        return metaBuilder.build();
    }

    /**
     * This is called when the current SessionState doesn't have a defined
     * chiptune or playlist and the user issues a play command to Chipper (via UI or widget, etc)
     * so we need to grab a random Chiptune from the list of all available chiptunes and set the
     * playlist as the master list (i.e. ALL THE CHIPTUNES) and start playback
     */
    private void coldStartRandomPlayback(){



    }

    /**
     * Shut down the this service
     */
    private void shutdown(){

        stopSelf();


    }

    /***********************************************************************************************
     *
     * Otto Subscriptions
     *
     */




    /***********************************************************************************************
     *
     * MediaSession Callbacks
     *
     */

    /**
     * Media Session Callbacks that listen to controller commands to control the music playback
     *
     */
    private MediaSessionCompat.Callback mMediaSessionCallbacks = new MediaSessionCompat.Callback() {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            // Handle custom command
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            if(mediaButtonEvent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)){
                KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                switch (event.getAction()){
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        play();
                        return true;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        pause();
                        return true;
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

                        return true;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        next();
                        return true;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        previous();
                        return true;
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        stop();
                        return true;
                }

            }

            return false;
        }

        @Override
        public void onPlay() {
            play();
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onSkipToNext() {
            next();
        }

        @Override
        public void onSkipToPrevious() {
            previous();
        }

        @Override
        public void onFastForward() {
            // Do Nothing
        }

        @Override
        public void onRewind() {
            // Do Nothing
        }

        @Override
        public void onStop() {
            stop();
        }

        @Override
        public void onSeekTo(long pos) {
            seek(pos);
        }

        @Override
        public void onSetRating(RatingCompat rating) {
            if(rating.getRatingStyle() == RatingCompat.RATING_THUMB_UP_DOWN){
                if(mCurrentState.isValid()) {
                    if (rating.isThumbUp()) {
                        mVoteManager.upvote(mCurrentState.getCurrentChiptune(), null);
                    } else {
                        mVoteManager.downvote(mCurrentState.getCurrentChiptune(), null);
                    }
                }
            }
        }
    };

    /***********************************************************************************************
     *
     * AudioPlayer Callbacks
     *
     */

    private AudioPlayer.SimplePlayerCallbacks mPlayerCallbacks = new AudioPlayer.SimplePlayerCallbacks() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Timber.e("MediaPlayer Error [what: %d][extra: %d]", what, extra);
            return false;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Timber.i("MediaPlayer Info [what: %d][extra: %d]", what, extra);
            return false;
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Timber.d("MediaPlayer Seek Complete [%d - %d]", mp.getCurrentPosition(), mp.getDuration());
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }
    };

    /***********************************************************************************************
     *
     * Receivers
     *
     */

    /**
     * This receiver catches the intent when a user unplugs a headset, or disconnects
     * from bluetooth
     */
    public BroadcastReceiver mNoisyAudioStreamReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                Timber.d("Becoming Noisy, pausing...");
                pause();
            }
        }
    };

    /**
     * The AudioManager focus change listener to listen for focus changes in the OS system
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    };
}
