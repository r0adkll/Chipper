package com.r0adkll.chipper.tv.ui.leanback.playlist;

import android.app.Activity;
import android.os.Bundle;

import com.activeandroid.Model;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.tv.ui.model.BaseTVActivity;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.tv.ui.leanback.playlist
 * Created by drew.heavner on 12/8/14.
 */
public class TVPlaylistActivity extends Activity{

    public static final String EXTRA_PLAYLIST = "extra_playlist_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_playlist);
        long playlistId = getIntent().getLongExtra(EXTRA_PLAYLIST, -1);

        // Load the playlist fragment
        TVPlaylistFragment details = (TVPlaylistFragment) getFragmentManager().findFragmentById(R.id.playlist_fragment);

        if(playlistId != -1){
            Playlist playlist = Model.load(Playlist.class, playlistId);
            details.setPlaylist(playlist);
        }

    }
}
