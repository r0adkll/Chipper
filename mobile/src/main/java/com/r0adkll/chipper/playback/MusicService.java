package com.r0adkll.chipper.playback;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by r0adkll on 11/25/14.
 */
public class MusicService extends Service implements AudioPlayer.PlayerCallbacks{

    /***********************************************************************************************
     *
     * Constants
     *
     */



    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject NotificationManagerCompat mNotificationManager;
    @Inject PlaylistManager mPlaylistManager;
    @Inject ChiptuneProvider mProvider;
    @Inject CashMachine mATM;
    @Inject Bus mBus;
    @Inject AudioPlayer mPlayer;

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
        mPlayer.setPlayerCallbacks(this);

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


    }

    /***********************************************************************************************
     *
     * Playback Methods
     *
     */

    private void play(){

    }

    private void pause(){

    }

    private void stop(){

    }

    private void next(){

    }

    private void previous(){

    }

    private void shuffle(boolean enabled){

    }

    private void repeat(int mode){

    }

    private void seek(long position){

    }


    /***********************************************************************************************
     *
     * Helper Methods
     *
     */



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

    private MediaSessionCompat.Callback mMediaSessionCallbacks = new MediaSessionCompat.Callback() {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {

        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {

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
                if(rating.isThumbUp()){
                    // TODO: Upvote

                }else{
                    // TODO: Downvote

                }
            }
        }
    };

    /***********************************************************************************************
     *
     * AudioPlayer Callbacks
     *
     */

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

}
