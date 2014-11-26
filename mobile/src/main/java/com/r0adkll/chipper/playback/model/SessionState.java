package com.r0adkll.chipper.playback.model;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.prefs.BooleanPreference;
import com.r0adkll.chipper.prefs.IntPreference;

/**
 * This class will contain the entire state of the play session operated by the
 * {@link com.r0adkll.chipper.playback.MusicService} class
 *
 * Created by r0adkll on 11/26/14.
 */
public class SessionState {

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

    private BooleanPreference mShufflePref;
    private IntPreference mRepeatPref;

    private Chiptune mChiptune;
    private Playlist mPlaylist;

    private boolean mIsShuffleEnabled = false;
    private int mRepeatMode = MODE_NONE;

    /**
     * Constructor
     */
    public SessionState(BooleanPreference shufflePref, IntPreference repeatPref){
        mShufflePref = shufflePref;
        mRepeatPref = repeatPref;

        // Load shuffle and repeat states
        mIsShuffleEnabled = mShufflePref.get();
        mRepeatMode = mRepeatPref.get();
    }

    /**
     * Return whether or not this current session state is
     * valid. I.E. if the chiptune and playlist have been set
     * @return
     */
    public boolean isValid(){
        return mChiptune != null && mPlaylist != null;
    }

    /**
     * Clear the session and make it invalid
     */
    public void clear(){
        mChiptune = null;
        mPlaylist = null;
    }

    /**
     * Get the current chiptune for this state
     * @return      the current chiptune
     */
    public Chiptune getCurrentChiptune(){
        return mChiptune;
    }

    /**
     * Get the current playlist for this state
     * @return      the current playlist
     */
    public Playlist getCurrentPlaylist(){
        return mPlaylist;
    }

    /**
     * Update this service state with new chiptune and playlist
     *
     * @param chiptune      the new current chiptune
     * @param playlist      the new(or old) current playlist
     */
    public void updateState(Chiptune chiptune, Playlist playlist){
        mChiptune = chiptune;
        mPlaylist = playlist;
    }

    /**
     * Update the current chiptune
     */
    public void updateCurrentChiptune(Chiptune chiptune){
        mChiptune = chiptune;
    }

    /**
     * Update the current playlist
     */
    public void updateCurrentPlaylist(Playlist playlist){
        mPlaylist = playlist;
    }

    /**
     * Return whether or not shuffle is enabled for this mode
     *
     * @return      the shuffle flag
     */
    public boolean isShuffleEnabled(){
        return mIsShuffleEnabled;
    }

    /**
     * Return the repeat mode for this session
     *
     * @see #MODE_NONE
     * @see #MODE_ONE
     * @see #MODE_ALL
     * @return      the repeat mode
     */
    public int getRepeatMode(){
        return mRepeatMode;
    }

    /**
     * Set the shuffle mode for this state
     * @param enabled
     */
    public void setShuffle(boolean enabled){
        mIsShuffleEnabled = enabled;
        mShufflePref.set(mIsShuffleEnabled);
    }

    /**
     * set the repeat mode for this state
     * @param mode
     */
    public void setRepeatMode(int mode){
        mRepeatMode = mode;
        mRepeatPref.set(mRepeatMode);
    }

}
