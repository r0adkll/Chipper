package com.r0adkll.chipper.ui.playlists;

import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistPresenterImpl implements PlaylistPresenter {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private PlaylistView mView;
    private ChipperService mService;
    private User mUser;

    /**
     * Constructor
     *
     * @param view
     * @param service
     * @param user
     */
    public PlaylistPresenterImpl(PlaylistView view, ChipperService service, User user) {
        mView = view;
        mService = service;
        mUser = user;
    }

    /***********************************************************************************************
     *
     * Presenter Methods
     *
     */


    @Override
    public void loadPlaylists() {

    }

    @Override
    public void addNewPlaylist(String name) {

    }

    @Override
    public void deletePlaylist(Playlist... playlists) {

    }

    @Override
    public void offlinePlaylist(Playlist playlist) {

    }

    @Override
    public void onPlaylistSelected(Playlist playlist, int position) {

    }
}
