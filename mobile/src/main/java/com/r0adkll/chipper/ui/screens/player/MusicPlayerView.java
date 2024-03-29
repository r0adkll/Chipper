package com.r0adkll.chipper.ui.screens.player;

import android.app.Activity;
import android.support.v4.media.session.MediaControllerCompat;

import com.r0adkll.chipper.api.model.Chiptune;

import java.util.List;

/**
 * Created by r0adkll on 12/1/14.
 */
public interface MusicPlayerView {

    /**
     * Get the view's activity
     *
     * @return      the activity for this view
     */
    public Activity getActivity();

    /**
     * Get the media controller used to comunicate with the
     * music playback service
     *
     * @return      the media controller
     */
    public MediaControllerCompat getMediaController();

    /**
     * Set the playback progress
     *
     * @param progress          the playback current position
     * @param duration          the playback total duration
     */
    public void setPlaybackProgress(int progress, int duration);

    /**
     * Set the title of the currently playing chiptune
     *
     * @param title     the title of the current playing chiptune
     */
    public void setTitle(String title);

    /**
     * Set the artist of the currently playing chiptune
     *
     * @param artist    the artist of the current playing chiptune
     */
    public void setArtist(String artist);

    /**
     * Set whether or not the current chiptune is playing
     *
     * @param value
     */
    public void setIsPlaying(boolean value);

    public void setShuffle(boolean value);

    public void setRepeat(int mode);

    public void setRating(int rating);

    public void setFavorited(boolean value);

    public void disableControls();

    public void enableControls();

    public void showSnackBar(String message);

    public void showSnackBar(String format, Object... args);

    public void setQueueList(List<Chiptune> chiptunes);
}
