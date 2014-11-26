package com.r0adkll.chipper.playback.model;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;

/**
 * This class will contain the entire state of the play session operated by the
 * {@link com.r0adkll.chipper.playback.MusicService} class
 *
 * Created by r0adkll on 11/26/14.
 */
public class ServiceState {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    /* Repeat Mode Constants */
    public static final int MODE_NONE = 0;
    public static final int MODE_ONE = 1;
    public static final int MODE_ALL = 2;


    /***********************************************************************************************
     *
     * Variables
     *
     */

    private Chiptune mChiptune;
    private Playlist mPlaylist;

    private boolean mIsShuffleEnabled = false;
    private int mRepeatMode = MODE_NONE;

    private PlaybackState mPlaybackState;


}
