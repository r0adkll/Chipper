package com.r0adkll.chipper.ui.player;

import android.app.Activity;
import android.support.v4.media.session.MediaControllerCompat;

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


}
