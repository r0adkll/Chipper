package com.r0adkll.chipper.ui.player;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.playback.events.PlayProgressEvent;
import com.r0adkll.chipper.playback.events.PlayQueueEvent;
import com.r0adkll.chipper.playback.model.PlayQueue;

/**
 * Created by r0adkll on 12/1/14.
 */
public interface MusicPlayerPresenter {

    public void previous();

    public void playPause();

    public void next();

    public void shuffle();

    public void repeat();

    public void seek(long position);

    public void upvote();

    public void downvote();

    public void favorite();

    public void add();

    public void onQueueItemSelected(Chiptune chiptune);

    public void onSessionDestroyed();

    public void onPlayProgressEvent(PlayProgressEvent event);

    public void onPlayQueueEvent(PlayQueueEvent queue);

    public void onSessionEvent(String event, Bundle extras);

    public void onPlaybackStateChanged(PlaybackStateCompat state);

    public void onMetadataChanged(MediaMetadataCompat metadata);

}
