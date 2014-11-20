package com.r0adkll.chipper.ui.playlists.viewer;

import android.content.Intent;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.model.ModelLoader;
import com.r0adkll.chipper.data.model.OfflineRequest;

import java.util.Arrays;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistViewerPresenterImpl implements PlaylistViewerPresenter {

    private PlaylistViewerView mView;
    private ChipperService mService;
    private User mUser;

    /**
     * Constructor
     *
     * @param view
     * @param service
     * @param user
     */
    public PlaylistViewerPresenterImpl(PlaylistViewerView view, ChipperService service, User user) {
        mView = view;
        mService = service;
        mUser = user;
    }

    @Override
    public void onPlaySelected(Playlist playlist) {

    }

    @Override
    public void onChiptuneSelected(Chiptune chiptune) {

    }

    @Override
    public void upvoteChiptune(Chiptune chiptune) {

    }

    @Override
    public void downvoteChiptune(Chiptune chiptune) {

    }

    @Override
    public void favoriteChiptunes(Chiptune... chiptunes) {

    }

    @Override
    public void addChiptunesToPlaylist(Playlist playlist, Chiptune... chiptunes) {

    }

    @Override
    public void offlineChiptunes(Chiptune... chiptunes) {
        // Create an offline task
        OfflineRequest request = new OfflineRequest.Builder()
                .addChiptunes(Arrays.asList(chiptunes))
                .build();

        // Send offline request
        Intent offlineIntent = OfflineRequest.createOfflineRequestIntent(mView.getActivity(), request);
        mView.getActivity().startService(offlineIntent);
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
    public ModelLoader<ChiptuneReference> getLoader(Playlist playlist) {
        From query = new Select()
                .from(ChiptuneReference.class)
                .where("playlist=?", playlist.getId());

        return new ModelLoader<>(mView.getActivity(), ChiptuneReference.class, query, true);
    }
}
