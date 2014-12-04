package com.r0adkll.chipper.ui.playlists.viewer;

import android.app.Activity;

import com.r0adkll.chipper.api.model.Playlist;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface PlaylistViewerView {

    public void showProgress();

    public void hideProgress();

    public void showErrorMessage(String msg);

    public Activity getActivity();

    public Playlist getPlaylist();

}
