package com.r0adkll.chipper.ui.screens.playlists;

import android.app.Activity;

import com.r0adkll.chipper.api.model.Playlist;

import java.util.List;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface PlaylistView {

    public void setPlaylists(List<Playlist> playlists);

    public void setSharedPlaylists(List<Playlist> sharedPlaylists);

    public void showProgress();

    public void hideProgress();

    public void showErrorMessage(String msg);

    public Activity getActivity();

}
