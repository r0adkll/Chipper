package com.r0adkll.chipper.ui.playlists.viewer;

import android.app.Activity;

import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.ui.model.IView;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface PlaylistViewerView extends IView {

    public void showProgress();

    public void hideProgress();

    public void showErrorMessage(String msg);

    public Playlist getPlaylist();

}
