package com.r0adkll.chipper.ui.playlists.viewer;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.model.ModelLoader;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface PlaylistViewerPresenter {

    public void onPlaySelected(Playlist playlist);

    public void onChiptuneSelected(Chiptune chiptune);

    public void upvoteChiptune(Chiptune chiptune);

    public void downvoteChiptune(Chiptune chiptune);

    public void favoriteChiptunes(Chiptune... chiptunes);

    public void addChiptunesToPlaylist(Playlist playlist, Chiptune... chiptunes);

    public void offlineChiptunes(Chiptune... chiptunes);

    public void offlinePlaylist(Playlist playlist);

    public ModelLoader<ChiptuneReference> getLoader(Playlist playlist);

}
