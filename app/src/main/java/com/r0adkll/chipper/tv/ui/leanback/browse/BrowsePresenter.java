package com.r0adkll.chipper.tv.ui.leanback.browse;

import android.content.Loader;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;

/**
 * Created by r0adkll on 12/7/14.
 */
public interface BrowsePresenter {

    public void loadChiptunes();

    public void loadPlaylists();

    public void onChiptuneClicked(Chiptune chiptune);

    public void onPlaylistClicked(Playlist playlist);

}
