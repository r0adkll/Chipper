package com.r0adkll.chipper.ui.playlists;

import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.model.ModelLoader;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface PlaylistPresenter {

    public void loadPlaylists();

    public void loadSharedPlaylists();

    public void addNewPlaylist(String name);

    public void deletePlaylist(Playlist... playlists);

    public void offlinePlaylist(Playlist playlist);

    public void onPlaylistSelected(Playlist playlist, int position);

    public ModelLoader<Playlist> getLoader();

}
