package com.r0adkll.chipper.ui.screens.playlists;

import android.view.View;

import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.model.ModelLoader;

import java.util.Collection;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface PlaylistPresenter {

    public void loadSharedPlaylists();

    public void addNewPlaylist(String name);

    public void deletePlaylist(Collection<Playlist> playlists);

    public void offlinePlaylist(Playlist playlist);

    public void onPlaylistSelected(View view, Playlist playlist, int position);

    public ModelLoader<Playlist> getLoader();

}
