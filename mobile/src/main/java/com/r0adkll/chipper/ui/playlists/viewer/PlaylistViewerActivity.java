package com.r0adkll.chipper.ui.playlists.viewer;

import android.content.Intent;
import android.os.Bundle;

import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.ui.model.BaseActivity;

import icepick.Icepick;
import icepick.Icicle;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistViewerActivity extends BaseActivity {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final String EXTRA_PLAYLIST = "extra_playlist";

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Icicle
    Playlist mPlaylist;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        // Set the content view of this activity
        setContentView(0);

        // Load sent playlist to view
        Intent intent = getIntent();
        if(intent != null && mPlaylist == null){
            mPlaylist = intent.getParcelableExtra(EXTRA_PLAYLIST);
        }

        // Check to see if we found a playlist, if not kill this activity
        if(mPlaylist == null) finish();

        // Now present the layout

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected Object[] getModules() {
        return new Object[0];
    }

}
