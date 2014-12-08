package com.r0adkll.chipper.tv.ui.leanback.browse;

import android.content.Loader;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.utils.ChiptuneComparator;

import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by r0adkll on 12/7/14.
 */
public class BrowsePresenterImpl implements BrowsePresenter {

    private BrowseView mView;

    private User mCurrentUser;
    private ChiptuneProvider mProvider;
    private PlaylistManager mPlaylistManager;

    /**
     * Constructor
     */
    public BrowsePresenterImpl(BrowseView view,
                               User user,
                               ChiptuneProvider provider,
                               PlaylistManager playlistManager){
        mView = view;
        mCurrentUser = user;
        mProvider = provider;
        mPlaylistManager = playlistManager;
    }


    @Override
    public void loadChiptunes() {
        mView.showProgress();
        mProvider.loadChiptunes(new Callback<List<Chiptune>>() {
            @Override
            public void success(List<Chiptune> chiptunes, Response response) {
                mView.hideProgress();
                setChiptunes(chiptunes);
            }

            @Override
            public void failure(RetrofitError error) {
                mView.hideProgress();
                handleRetrofitError(error);
            }
        });
    }

    @Override
    public void loadPlaylists() {
        mView.setPlaylists(mCurrentUser.getPlaylists());
    }

    /**
     * Sort and Send the chiptune list to the view
     *
     * @param chiptunes     the list of chiptunes to display
     */
    private void setChiptunes(List<Chiptune> chiptunes){

        // 1. Sort
        Collections.sort(chiptunes, new ChiptuneComparator());

        // 2. Send to view
        mView.setChiptunes(chiptunes);

    }


    /**
     * Handle the retrofit error from the chipper api
     * @param error
     */
    private void handleRetrofitError(RetrofitError error){
        mView.showErrorMessage(error.getLocalizedMessage());
    }

}
