package com.r0adkll.chipper.ui.playlists;

import android.content.Intent;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.model.ModelLoader;
import com.r0adkll.chipper.data.model.OfflineRequest;
import com.r0adkll.chipper.ui.playlists.viewer.PlaylistViewerActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

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
    public void loadSharedPlaylists() {

        mService.getSharedPlaylists(mUser.id, new Callback<List<Playlist>>() {
            @Override
            public void success(List<Playlist> playlists, Response response) {
                mView.setSharedPlaylists(playlists);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error.getCause(), "Unable to fetch user's shared playlists: %s", error.getKind().toString());
            }
        });

    }

    @Override
    public void addNewPlaylist(String name) {
        // Use the playlist manager to create a new playlist
        mManager.createPlaylist(name);
    }

    @Override
    public void deletePlaylist(Collection<Playlist> playlists) {
        final List<Playlist> plists = new ArrayList<>(mManager.deletePlaylists(playlists));
        if(!plists.isEmpty()){
            String text = plists.size() == 1 ?
                    String.format("%s was deleted", plists.get(0).name) :
                    String.format("%d playlists were deleted", plists.size());

            Snackbar.with(mView.getActivity())
                    .text(text)
                    .actionLabel("UNDO")
                    .actionColor(mView.getActivity().getResources().getColor(R.color.primaryDark))
                    .actionListener(new ActionClickListener() {
                        @Override
                        public void onActionClicked() {
                            // Resave all the playlists
                            for(Playlist plist: plists){
                                Playlist.create(plist);
                            }
                        }
                    })
                    .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                    .show(mView.getActivity());
        }
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
        Intent intent = new Intent(mView.getActivity(), PlaylistViewerActivity.class);
        intent.putExtra(PlaylistViewerActivity.EXTRA_PLAYLIST_ID, playlist.getId());
        mView.getActivity().startActivity(intent);
    }

    @Override
    public ModelLoader<Playlist> getLoader() {
        From query = new Select()
                .from(Playlist.class)
                .where("owner=?", mUser.getId());
        return new ModelLoader<>(mView.getActivity(), Playlist.class, query, true);
    }
}
