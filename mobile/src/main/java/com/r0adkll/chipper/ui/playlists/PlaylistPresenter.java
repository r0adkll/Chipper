package com.r0adkll.chipper.ui.playlists;

import com.r0adkll.chipper.core.api.model.Playlist;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface PlaylistPresenter {

    public void loadPlaylists();

    public void addNewPlaylist(String name);

    public void deletePlaylist(Playlist... playlists);

    public void offlinePlaylist(Playlist playlist);

    public void onPlaylistSelected(Playlist playlist, int position);

}
