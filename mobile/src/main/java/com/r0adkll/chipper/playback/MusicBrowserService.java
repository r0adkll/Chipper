package com.r0adkll.chipper.playback;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.service.media.MediaBrowserService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import com.activeandroid.Model;
import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.playback.events.MediaSessionEvent;
import com.r0adkll.chipper.playback.events.PlayProgressEvent;
import com.r0adkll.chipper.playback.events.PlayQueueEvent;
import com.r0adkll.chipper.playback.model.AudioSession;
import com.r0adkll.chipper.playback.model.PlayQueue;
import com.r0adkll.chipper.playback.model.SessionState;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.utils.prefs.IntPreference;
import com.r0adkll.chipper.qualifiers.SessionRepeatPreference;
import com.r0adkll.chipper.qualifiers.SessionShufflePreference;
import com.r0adkll.chipper.tv.ui.leanback.playback.TVPlaybackActivity;
import com.r0adkll.chipper.ui.Chipper;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.deadskunk.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import java.util.List;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/25/14.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MusicBrowserService extends MediaBrowserService {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final String MEDIA_SESSION_TAG = "Chipper Session";

    public static final String INTENT_ACTION_PLAY = "com.r0adkll.chipper.intent.action.START_PLAYBACK";
    public static final String INTENT_ACTION_COLDSTART = "com.r0adkll.chipper.intent.action.COLDSTART_PLAYBACK";

    public static final String ACTION_PLAY = "com.r0adkll.chipper.intent.PLAY";
    public static final String ACTION_PAUSE = "com.r0adkll.chipper.intent.PAUSE";
    public static final String ACTION_PLAYPAUSE = "com.r0adkll.chipper.intent.PLAYPAUSE";
    public static final String ACTION_SHUFFLEPLAY = "com.r0adkll.chipper.action.WIDGET_SHUFFLE_PLAY";
    public static final String ACTION_NEXT = "com.r0adkll.chipper.intent.NEXT";
    public static final String ACTION_PREV = "com.r0adkll.chipper.intent.PREV";
    public static final String ACTION_EXIT = "com.r0adkll.chipper.intent.EXIT";
    public static final String ACTION_UPVOTE = "com.r0adkll.chipper.intent.UPVOTE";
    public static final String ACTION_DOWNVOTE = "com.r0adkll.chipper.intent.DOWNVOTE";

    public static final String COMMAND_ENABLE_NOTIFICATION = "com.r0adkll.chipper.command.ENABLE_NOTIFICATION";
    public static final String COMMAND_DISABLE_NOTIFICATION = "com.r0adkll.chipper.command.DISABLE_NOTIFICATION";
    public static final String COMMAND_SHOW_NOTIFICATION = "com.r0adkll.chipper.command.SHOW_NOTIFICATION";
    public static final String COMMAND_SHUFFLE = "com.r0adkll.chipper.command.SHUFFLE";
    public static final String COMMAND_REPEAT = "com.r0adkll.chipper.command.REPEAT";
    public static final String COMMAND_QUEUE_JUMP = "com.r0adkll.chipper.command.QUEUE_JUMP";

    public static final String EVENT_PLAY_PROGRESS_UPDATED = "com.r0adkll.chipper.event.PROGRESS_CHANGE";

    public static final String EXTRA_CHIPTUNE = "com.r0adkll.chipper.extra.CHIPTUNE";
    public static final String EXTRA_PLAYLIST = "com.r0adkll.chipper.extra.PLAYLIST";
    public static final String EXTRA_SHUFFLE = "com.r0adkll.chipper.extra.SHUFFLE";
    public static final String EXTRA_REPEAT = "com.r0adkll.chipper.extra.REPEAT";
    public static final String EXTRA_CURRENT_POSITION = "com.r0adkll.chipper.extra.CURRENT_POSITION";
    public static final String EXTRA_TOTAL_DURATION = "com.r0adkll.chipper.extra.TOTAL_DURATION";

    public static final String METADATA_KEY_FAVORITED = "com.r0adkll.chipper.metadata.FAVORITED";

    private static final float DUCK_VOLUME_LEVEL = 0.25f;
    private static final int PREVIOUS_TIME_CUTOFF = 5 * 1000; // 5 seconds
    private static final int NOTIFICATION_ID = 100;

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject AudioManager mAudioManager;
    @Inject NotificationManagerCompat mNotificationManagerCompat;
    @Inject NotificationManager mNotificationManager;
    @Inject PlaylistManager mPlaylistManager;
    @Inject VoteManager mVoteManager;
    @Inject ChiptuneProvider mProvider;
    @Inject CashMachine mATM;
    @Inject Bus mBus;

    @Inject @SessionShufflePreference BooleanPreference mShufflePref;
    @Inject @SessionRepeatPreference IntPreference mRepeatPref;
    @Inject AudioPlayer mPlayer;

    private final Handler mHandler = new Handler();
    private PlayQueue mQueue;
    private SessionState mCurrentState;
    private MediaSessionCompat mCurrentSession;

    private boolean mCanShowNotification = true;
    private Bitmap mArt;
    private Bitmap mArtAlbum;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        ChipperApp.get(this).inject(this);

        // Load art
        mArt = BitmapFactory.decodeResource(getResources(), R.drawable.login_background);
        mArtAlbum = BitmapFactory.decodeResource(getResources(), R.drawable.chipper_round_watch_bg);

        // Register for the Otto bus
        mBus.register(this);

        // Setup the Player Callbacks
        mPlayer.setPlayerCallbacks(mPlayerCallbacks);

        // Register Receivers
        IntentFilter remoteActionFilter = new IntentFilter();
        remoteActionFilter.addAction(ACTION_PLAY);
        remoteActionFilter.addAction(ACTION_PAUSE);
        remoteActionFilter.addAction(ACTION_PLAYPAUSE);
        remoteActionFilter.addAction(ACTION_NEXT);
        remoteActionFilter.addAction(ACTION_PREV);
        remoteActionFilter.addAction(ACTION_UPVOTE);
        remoteActionFilter.addAction(ACTION_DOWNVOTE);
        remoteActionFilter.addAction(ACTION_EXIT);
        registerReceiver(mRemoteActionReceiver, remoteActionFilter);
        registerReceiver(mNoisyAudioStreamReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        // Initialize the Session State
        mCurrentState = new SessionState(mShufflePref, mRepeatPref);

        Intent main = new Intent(this, TVPlaybackActivity.class);
        PendingIntent mainPI = PendingIntent.getActivity(this, 0, main, PendingIntent.FLAG_UPDATE_CURRENT);

        // Initialize the Media Session
        mCurrentSession = new MediaSessionCompat(this, MEDIA_SESSION_TAG);
        mCurrentSession.setCallback(mMediaSessionCallbacks);
        mCurrentSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mCurrentSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);

        MediaSession session = (MediaSession) mCurrentSession.getMediaSession();
        session.setSessionActivity(mainPI);

        // Set teh session token
        setSessionToken((android.media.session.MediaSession.Token) mCurrentSession.getSessionToken().getToken());

        // Post the session
        mBus.post(new MediaSessionEvent(mCurrentSession));
    }

    /**
     * Called when this service is started with an intent
     *
     * @param intent        the intent this service was started with
     * @param flags         the flags this service was started with
     * @param startId       the start count
     * @return              {@link #START_STICKY}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("MusicService::onStartCommand(%s, %d, %d)", intent, flags, startId);

        // Handle intent
        if(intent != null) {
            switch (intent.getAction()) {
                case INTENT_ACTION_PLAY:
                    Bundle xtras = intent.getExtras();
                    if (xtras != null) {

                        // Parse the Chiptune and Playlist(optional) from the extras
                        long chiptuneId = xtras.getLong(EXTRA_CHIPTUNE, -1);
                        long playlistId = xtras.getLong(EXTRA_PLAYLIST, -1);

                        // If a chiptune was found, determine if playlist was sent as well
                        if (chiptuneId != -1) {
                            // Get the Chiptune from DB
                            Chiptune chiptune = Model.load(Chiptune.class, chiptuneId);

                            if (playlistId != -1) {
                                Playlist playlist = Model.load(Playlist.class, playlistId);
                                mQueue = new PlayQueue(mProvider, chiptune, playlist);
                            } else {
                                mQueue = new PlayQueue(mProvider, chiptune);
                            }

                            // Set session as active
                            mCurrentSession.setActive(true);

                            // Post Session Queue change event
                            mBus.post(new PlayQueueEvent(mQueue, mCurrentState));

                            // Start playback
                            startPlayback();
                        }
                    }
                    break;
                case INTENT_ACTION_COLDSTART:
                    coldStartRandomPlayback();
                    break;
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowser.MediaItem>> result) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Un-register from the otto bus
        mBus.unregister(this);

        // Unregister Receivers
        unregisterReceiver(mNoisyAudioStreamReceiver);
        unregisterReceiver(mRemoteActionReceiver);

        // Relinquish playback resources
        stop();

        // Dismiss the playback notification
        dismissNotification();

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
        showNotification();
    }

    private void pause(){
        // Pause Playback
        mPlayer.pause();
        mCurrentSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_PAUSED));

        // Update Notification
        showNotification();
    }

    /**
     * Play/Pause toggle function. If the player is setup and playing/paused it will toggle the
     * play and pause states. Otherwise it will initiate a cold start random playback
     */
    private void playPause(){
        if(mQueue != null){
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

    /**
     * Stop the playback and release immediate resources
     */
    private void stop(){
        mPlayer.stop();
        mPlayer.release();
        mCurrentSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_STOPPED));
        mHandler.removeCallbacks(mPlayProgressUpdater);
    }

    /**
     * Start playing the next chiptune in the queue
     */
    private void next(){
        if(mQueue != null){
            mQueue.next(mCurrentState, true);
            startPlayback();

            // Post Session Queue change event
            mBus.post(new PlayQueueEvent(mQueue, mCurrentState));
        }
    }

    /**
     * Start playing the previous chiptune in the queue, or if
     * the playback is old enough, restart the current chiptune
     */
    private void previous(){
        if(mQueue != null){

            // Check playback time
            if(mPlayer.isPlaying()){
                int progress = mPlayer.getCurrentPosition();

                // if the playback is less than X seconds long, go to previous, otherwise restart the current.
                if(progress > PREVIOUS_TIME_CUTOFF){
                    seek(0);
                }else{
                    mQueue.previous(mCurrentState);
                    startPlayback();

                    // Post Session Queue change event
                    mBus.post(new PlayQueueEvent(mQueue, mCurrentState));
                }
            }else{
                mQueue.previous(mCurrentState);
                startPlayback();

                // Post Session Queue change event
                mBus.post(new PlayQueueEvent(mQueue, mCurrentState));
            }

        }
    }

    /**
     * Shuffle the current playback queue and save to the preferences
     *
     * @param enabled       shuffle flag
     */
    private void shuffle(boolean enabled){
        mCurrentState.setShuffle(enabled);

        // Re shuffle the queue
        if(enabled){
            if(mQueue != null){
                mQueue.shuffle();
            }
        }

        // Post Session Queue change event
        mBus.post(new PlayQueueEvent(mQueue, mCurrentState));
    }

    /**
     * Update the repeat mode
     *
     * @see com.r0adkll.chipper.playback.model.SessionState#MODE_ONE
     * @see com.r0adkll.chipper.playback.model.SessionState#MODE_ALL
     * @see com.r0adkll.chipper.playback.model.SessionState#MODE_NONE
     * @param mode  the repeat mode
     */
    private void repeat(int mode){
        mCurrentState.setRepeatMode(mode);

        // Post Session Queue change event
        mBus.post(new PlayQueueEvent(mQueue, mCurrentState));
    }

    /**
     * Seek to a certain position in hte playback
     * @param position
     */
    private void seek(int position){
        mPlayer.seekTo(position);
    }

    /**
     * Send an event to let the controllers know to update their UI with updated playback state
     * and metadata information already available in their system
     */
    private void publishPlayProgress(){
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getTotalDuration();

        mBus.post(new PlayProgressEvent(position, duration));
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Starting playing the current item in the play queue
     *
     */
    private void startPlayback(){

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
            mCurrentSession.setMetadata(buildMetaData());

            // Get teh current chiptune from the queue to play
            Chiptune chiptune = mQueue.current(mCurrentState);

            // Build audio session to play
            AudioSession session = new AudioSession(mATM, chiptune);
            mPlayer.prepareAudioSession(session, new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    // Initiate Playback
                    play();

                    // Update playback state and metadata
                    mCurrentSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_PLAYING));

                    // Update Notification
                    showNotification();

                    // Execute Play progress update handler loop
                    mHandler.postDelayed(mPlayProgressUpdater, 200);

                    // TODO: Update Widget


                }
            }, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    // Update Player State
                    mPlayer.setCurrentState(AudioPlayer.COMPLETED);

                    // Kill Play Progress update handler
                    mHandler.removeCallbacks(mPlayProgressUpdater);

                    // Attempt to get the next chiptune and play it
                    Chiptune next = mQueue.next(mCurrentState, false);
                    if(next != null){
                        startPlayback();

                        // Post Session Queue change event
                        mBus.post(new PlayQueueEvent(mQueue, mCurrentState));
                    }else{
                        stop();

                        // Dismiss notification
                        dismissNotification();

                        // Relenquish focus
                        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);

                        // Publish State
                        mCurrentSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_STOPPED));

                    }

                }
            });

        }else{
            Timber.e("Audio Focus Request Failed: %d", result);
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
        Chiptune chiptune = mQueue.current(mCurrentState);

        // Create the builder
        MediaMetadataCompat.Builder metaBuilder = new MediaMetadataCompat.Builder();

        if(chiptune != null){

            int voteValue = mVoteManager.getUserVoteValue(chiptune.id);
            RatingCompat rating;
            if(voteValue != 0) {
                rating = RatingCompat.newThumbRating(voteValue == 1 ? true : false);
            }else{
                rating = RatingCompat.newUnratedRating(RatingCompat.RATING_THUMB_UP_DOWN);
            }

            metaBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, chiptune.artist)
                       .putString(MediaMetadataCompat.METADATA_KEY_TITLE, chiptune.title)
                       .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, chiptune.length)
                       .putRating(MediaMetadataCompat.METADATA_KEY_USER_RATING, rating)
                       .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, mArtAlbum)
                       .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, mArt)
                       .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, mArtAlbum)
                       .putString(METADATA_KEY_FAVORITED, String.valueOf(mPlaylistManager.isFavorited(chiptune)));

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

        // Get a random chiptune, and construct the play queue from it
        Chiptune randomChiptune = mProvider.getRandomChiptune();
        mQueue = new PlayQueue(mProvider, randomChiptune);

        // Set session as active
        mCurrentSession.setActive(true);

        // Start playback of the current queue
        startPlayback();

        // Post Session Queue change event
        mBus.post(new PlayQueueEvent(mQueue, mCurrentState));

        // Update the shuffle
        shuffle(true);

    }

    /**
     * Shut down the this service
     */
    private void shutdown(){
        stopSelf();

    }

    @SuppressLint("NewApi")
    private void showNotification(){

        if(mQueue != null && mCanShowNotification) {

            Chiptune current = mQueue.current(mCurrentState);

            // Build info
            String title = current.title;
            String text = current.artist;

            // Build Content and Delete PendingIntents
            Intent deleteIntent = new Intent(ACTION_EXIT);
            PendingIntent deletePI = PendingIntent.getBroadcast(this, 0, deleteIntent, 0);

            Intent contentIntent = new Intent(this, Chipper.class);
            PendingIntent contentPI = PendingIntent.getActivity(this, 0, contentIntent, 0);

            if(Utils.isLollipop()) {

                // Build wear extender
                Notification.WearableExtender wearableExtender =
                        new Notification.WearableExtender()
                                .setHintHideIcon(true)
                                .setBackground(mArtAlbum);

                // Build the notification
                Notification.Builder builder = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_chipper)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(contentPI)
                        .setDeleteIntent(deletePI)
                        .setOngoing(mPlayer.isPlaying())
                        .extend(wearableExtender)
                        .setStyle(new Notification.MediaStyle()
                                .setMediaSession((android.media.session.MediaSession.Token) mCurrentSession.getSessionToken().getToken())
                                .setShowActionsInCompactView(2, 3));

                // Build actions/intents
                Notification.Action play = buildActionLollipop(R.drawable.ic_action_notif_play, R.string.action_play, ACTION_PLAY);
                Notification.Action pause = buildActionLollipop(R.drawable.ic_action_notif_pause, R.string.action_pause, ACTION_PAUSE);
                Notification.Action next = buildActionLollipop(R.drawable.ic_action_notif_next, R.string.action_next, ACTION_NEXT);
                Notification.Action previous = buildActionLollipop(R.drawable.ic_action_notif_previous, R.string.action_previous, ACTION_PREV);
                Notification.Action upvote = buildActionLollipop(R.drawable.ic_action_notif_thumb_up, R.string.action_upvote, ACTION_UPVOTE);
                Notification.Action downvote = buildActionLollipop(R.drawable.ic_action_notif_thumb_down, R.string.action_downvote, ACTION_DOWNVOTE);

                // Build the actions
                builder.addAction(downvote)
                        .addAction(previous)
                        .addAction(mPlayer.isPlaying() ? pause : play)
                        .addAction(next)
                        .addAction(upvote);

                // Show notification
                mNotificationManager.notify(NOTIFICATION_ID, builder.build());

            }else{

                // Build wear extender
                NotificationCompat.WearableExtender wearableExtender =
                        new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(mArtAlbum);

                // Build the notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_chipper)
                        .setColor(getResources().getColor(R.color.primary))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(contentPI)
                        .setDeleteIntent(deletePI)
                        .setOngoing(mPlayer.isPlaying())
                        .extend(wearableExtender);

                // Build actions/intents
                Action play = buildAction(R.drawable.ic_action_play, R.string.action_play, ACTION_PLAY);
                Action pause = buildAction(R.drawable.ic_action_pause, R.string.action_pause, ACTION_PAUSE);
                Action next = buildAction(R.drawable.ic_action_skip_next, R.string.action_next, ACTION_NEXT);
                Action previous = buildAction(R.drawable.ic_action_skip_previous, R.string.action_previous, ACTION_PREV);

                // Build teh actions
                builder.addAction(previous)
                       .addAction(mPlayer.isPlaying() ? pause : play)
                       .addAction(next);

                // Show notification
                mNotificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
            }

        }
    }

    /**
     * Dismiss the current playback notification
     */
    private void dismissNotification(){
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * Build a remote action pending intent
     *
     * @param action        the action to perform
     * @return              the associated pending intent
     */
    private PendingIntent buildRemoteAction(String action){
        Intent intent = new Intent(action);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    /**
     * Build a remote action notification action
     *
     * @param icon      the icon resource to use
     * @param title     the title string resource to use
     * @param action    the associated remote action
     * @return          the built notification action item
     */
    private Action buildAction(int icon, int title, String action){
        return new Action(icon, getString(title), buildRemoteAction(action));
    }

    /**
     * Build a remote action notification action
     *
     * @param icon      the icon resource to use
     * @param title     the title string resource to use
     * @param action    the associated remote action
     * @return          the built notification action item
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Notification.Action buildActionLollipop(int icon, int title, String action){
        return new Notification.Action(icon, getString(title), buildRemoteAction(action));
    }

    /***********************************************************************************************
     *
     * Otto Subscriptions
     *
     */


    /**
     * Produce the current media session if available
     *
     * @return      the available media session event
     */
    @Produce
    public MediaSessionEvent produceMediaSession(){
        if(mCurrentSession != null){
            return new MediaSessionEvent(mCurrentSession);
        }
        return null;
    }

    /**
     * Produce a play queue event if it exists
     *
     * @return      the play queue event
     */
    @Produce
    public PlayQueueEvent producePlayQueueEvent(){
        if(mQueue != null){
            return new PlayQueueEvent(mQueue, mCurrentState);
        }
        return null;
    }

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

        @DebugLog
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            Timber.i("onCommand(%s, %s)", command, extras.toString());
            // Handle custom command
            switch (command){
                case ACTION_PLAYPAUSE:
                    playPause();
                    break;
                case ACTION_SHUFFLEPLAY:
                    coldStartRandomPlayback();
                    break;
                case ACTION_EXIT:
                    shutdown();
                    break;
                case COMMAND_DISABLE_NOTIFICATION:
                    mCanShowNotification = false;
                    dismissNotification();
                    break;
                case COMMAND_ENABLE_NOTIFICATION:
                    mCanShowNotification = true;
                    break;
                case COMMAND_SHOW_NOTIFICATION:
                    mCanShowNotification = true;
                    showNotification();
                    break;
                case COMMAND_SHUFFLE:
                    // Get shuffle value from data
                    boolean shuffle = extras.getBoolean(EXTRA_SHUFFLE, false);
                    shuffle(shuffle);
                    break;
                case COMMAND_REPEAT:
                    int repeat = extras.getInt(EXTRA_REPEAT, SessionState.MODE_NONE);
                    repeat(repeat);
                    break;
                case COMMAND_QUEUE_JUMP:
                    String chiptuneId = extras.getString(EXTRA_CHIPTUNE);
                    if(chiptuneId != null){
                        Chiptune chiptune = mProvider.getChiptune(chiptuneId);

                        // Re-index queue
                        mQueue.reIndex(chiptune);

                        // Start playing new index
                        startPlayback();

                        // Post queue event
                        mBus.post(new PlayQueueEvent(mQueue, mCurrentState));
                    }
                    break;
            }
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            if(mediaButtonEvent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)){
                KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if(event.getAction() == KeyEvent.ACTION_UP) {
                    Timber.i("MediaButtonEvent::onEvent(action: %d, keycode: %d)");

                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            play();
                            return true;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            pause();
                            return true;
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            playPause();
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
            seek((int)pos);
        }

        @DebugLog
        @Override
        public void onSetRating(RatingCompat rating) {
            if(rating.getRatingStyle() == RatingCompat.RATING_THUMB_UP_DOWN){
                if(mQueue != null) {
                    if (rating.isThumbUp()) {
                        mVoteManager.upvote(mQueue.current(mCurrentState), new CallbackHandler() {
                            @Override
                            public void onHandle(Object value) {
                                mCurrentSession.setMetadata(buildMetaData());
                            }

                            @Override
                            public void onFailure(String msg) {
                                Timber.e("Unable to upvote from MediaSession: %s", msg);
                            }
                        });
                    } else {
                        mVoteManager.downvote(mQueue.current(mCurrentState), new CallbackHandler() {
                            @Override
                            public void onHandle(Object value) {
                                mCurrentSession.setMetadata(buildMetaData());
                            }

                            @Override
                            public void onFailure(String msg) {
                                Timber.e("Unable to upvote from MediaSession: %s", msg);
                            }
                        });
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

    /**
     * The AudioPlayer callbacks for the audio player. these are called on info, error, seek, and
     * buffering events.
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
            // Update UI

        }
    };

    /***********************************************************************************************
     *
     * Receivers
     *
     */

    /**
     * The BroadcastReceiver for all the pending intent actions from the notifications,
     * or widgets
     */
    public BroadcastReceiver mRemoteActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case ACTION_PLAY:
                    play();
                    break;
                case ACTION_PAUSE:
                    pause();
                    break;
                case ACTION_PLAYPAUSE:
                    playPause();
                    break;
                case ACTION_NEXT:
                    next();
                    break;
                case ACTION_PREV:
                    previous();
                    break;
                case ACTION_UPVOTE:
                    mVoteManager.upvote(mQueue.current(mCurrentState), null);
                    break;
                case ACTION_DOWNVOTE:
                    mVoteManager.downvote(mQueue.current(mCurrentState), null);
                    break;
                case ACTION_SHUFFLEPLAY:
                    coldStartRandomPlayback();
                    break;
                case ACTION_EXIT:
                    shutdown();
                    break;
            }
        }
    };

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
    boolean wasPlaying = false;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Timber.d( "Audio Focus Change: %d", focusChange);
            switch (focusChange){
                case AudioManager.AUDIOFOCUS_LOSS:
                    mAudioManager.abandonAudioFocus(this);
                    if(mPlayer.isPlaying()) wasPlaying = true;
                    pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if(mPlayer.isPlaying()) wasPlaying = true;
                    pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mPlayer.setVolume(DUCK_VOLUME_LEVEL, DUCK_VOLUME_LEVEL);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, 0);
                    // Resume playback
                    mPlayer.setVolume(1, 1);
                    if(wasPlaying){
                        play();
                        wasPlaying = false;
                    }
                    break;
            }
        }
    };

    /**
     * The Runnable that runs updates for play progress
     * updates
     *
     */
    private Runnable mPlayProgressUpdater = new Runnable() {
        @Override
        public void run() {

            // Dispatch Play Progress
            if(mPlayer.isPlaying()){
                publishPlayProgress();
            }

            // Rerun the handler execution of this runnable
            mHandler.postDelayed(mPlayProgressUpdater, 900);

        }
    };
}
