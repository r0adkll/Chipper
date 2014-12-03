package com.r0adkll.chipper.ui.player;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * Created by r0adkll on 12/1/14.
 */
public interface MusicPlayerPresenter {

    public void previous();

    public void playPause();

    public void next();

    public void shuffle();

    public void repeat();

    public void upvote();

    public void downvote();

    public void favorite();

    public void add();



    public void onSessionEvent(String event, Bundle extras);

    public void onPlaybackStateChanged(PlaybackStateCompat state);

    public void onMetadataChanged(MediaMetadataCompat metadata);

}
