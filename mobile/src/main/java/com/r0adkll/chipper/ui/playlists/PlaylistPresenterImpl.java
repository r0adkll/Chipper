package com.r0adkll.chipper.ui.playlists;

import android.content.Intent;

import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.data.PlaylistManager;
import com.r0adkll.chipper.core.data.model.OfflineRequest;

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
    private PlaylistManager mManager;
    private User mUser;

    /**
     * Constructor
     *
     * @param view
     * @param service
     * @param user
     */
    public PlaylistPresenterImpl(PlaylistView view,
                                 ChipperService service,
                                 PlaylistManager manager,
                                 User user) {
        mView = view;
        mService = service;
        mManager = manager;
        mUser = user;
    }

    /***********************************************************************************************
     *
     * Presenter Methods
     *
     */


    @Override
    public void loadPlaylists() {
        mView.setPlaylists(mUser.getPlaylists());
    }

    @Override
    public void addNewPlaylist(String name) {
        // Use the playlist manager to create a new playlist
        mManager.createPlaylist(name);
    }

    @Override
    public void deletePlaylist(Playlist... playlists) {
        mManager.deletePlaylists(playlists);
    }

    @Override
    public void offlinePlaylist(Playlist playlist) {

        // Create an offline task
        OfflineRequest request = new OfflineRequest.Builder()
                .addPlaylist(playlist)
                .build();

        // Send offline request
        Intent offlineIntent = OfflineRequest.createOfflineRequestIntent(mView.getActivity(), request);
        mView.getActivity().startService(offlineIntent);

    }

    @Override
    public void onPlaylistSelected(Playlist playlist, int position) {

    }
}
