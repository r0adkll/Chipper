package com.r0adkll.chipper.ui.all;

import android.widget.Toast;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.chipper.utils.ChiptuneComparator;

import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/13/14.
 */
public class ChiptunesPresenterImpl implements ChiptunesPresenter {

    private User mCurrentUser;
    private ChiptunesView mView;
    private ChipperService mService;
    private ChiptuneProvider mProvider;
    private PlaylistManager mPlaylistManager;
    private VoteManager mVoteManager;

    /**
     * Constructor
     *
     * @param view          the chipper view interface
     * @param service       the chipper API service
     */
    public ChiptunesPresenterImpl(ChiptunesView view,
                                  ChiptuneProvider provider,
                                  ChipperService service,
                                  PlaylistManager playlistManager,
                                  VoteManager voteManager,
                                  User user){
        mCurrentUser = user;
        mView = view;
        mService = service;
        mProvider = provider;
        mPlaylistManager = playlistManager;
        mVoteManager = voteManager;
    }

    @Override
    public void loadAllChiptunes() {
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
    public void onChiptuneSelected(Chiptune chiptune) {
        // Send Otto Event to start playing this selected chiptune
        Timber.i("Chiptune selected[%s]: %s-%s", chiptune.id, chiptune.artist, chiptune.title);
    }

    @Override
    public void upvoteChiptune(Chiptune chiptune) {
        final String title = chiptune.title;
        mVoteManager.upvote(chiptune, new CallbackHandler() {
            @Override
            public void onHandle(Object value) {
                // Update UI

            }

            @Override
            public void onFailure(String msg) {
                Timber.e("Unable to upvote %s : %s", title, msg);
            }
        });
    }

    @Override
    public void downvoteChiptune(Chiptune chiptune) {
        final String title = chiptune.title;
        mVoteManager.downvote(chiptune, new CallbackHandler() {
            @Override
            public void onHandle(Object value) {
                // Update UI

            }

            @Override
            public void onFailure(String msg) {
                Timber.e("Unable to downvote %s : %s", title, msg);
            }
        });
    }

    @Override
    public void favoriteChiptunes(Chiptune... chiptunes) {
        mPlaylistManager.addToFavorites(chiptunes);
    }

    @Override
    public void addChiptunesToPlaylist(final Chiptune... chiptunes) {
        mPlaylistManager.addToPlaylist(mView.getActivity(), new CallbackHandler() {
            @Override
            public void onHandle(Object value) {
                // Success fully added, Update UI

            }

            @Override
            public void onFailure(String msg) {
                if (msg != null) {
                    Timber.w("Unable to add chiptunes to playlist: %s", msg);
                }
            }
        }, chiptunes);
    }

    @Override
    public void offlineChiptunes(Chiptune... chiptunes) {
        CashMachine.offline(mView.getActivity(), chiptunes);
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
