package com.r0adkll.chipper.ui.playlists.viewer;

import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.model.ModelLoader;
import com.r0adkll.chipper.ui.model.IActionPresenter;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface PlaylistViewerPresenter extends IActionPresenter{

    public void onPlaySelected(Playlist playlist);

    public void offlinePlaylist(Playlist playlist);

    public ModelLoader<ChiptuneReference> getLoader(Playlist playlist);

}
