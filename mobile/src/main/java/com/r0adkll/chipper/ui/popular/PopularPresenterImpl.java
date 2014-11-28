package com.r0adkll.chipper.ui.popular;

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
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/15/14.
 */
public class PopularPresenterImpl implements PopularPresenter {

    private User mCurrentUser;
    private PopularView mView;
    private ChipperService mService;
    private ChiptuneProvider mProvider;
    private VoteManager mVoteManager;
    private PlaylistManager mPlaylistManager;

    /**
     * Constructor
     * @param view      the view interface
     * @param service   the api service
     */
    public PopularPresenterImpl(PopularView view,
                                ChiptuneProvider provider,
                                ChipperService service,
                                PlaylistManager playlistManager,
                                VoteManager voteManager,
                                User user){
        mView = view;
        mService = service;
        mCurrentUser = user;
        mProvider = provider;
        mVoteManager = voteManager;
        mPlaylistManager = playlistManager;
    }


    @Override
    public void loadAllChiptunes() {
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
    public void loadVotes() {

        mVoteManager.syncTotalVotes(new CallbackHandler<Map<String, Integer>>() {
            @Override
            public void onHandle(Map<String, Integer> votes) {
                mView.setVoteData(votes);
                mView.hideProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.showErrorMessage(msg);
                mView.hideProgress();
            }
        });

    }

    @Override
    public void onChiptuneSelected(Chiptune chiptune) {
        // Send to session to be played

    }

    @Override
    public void upvoteChiptune(final Chiptune chiptune) {
        mVoteManager.upvote(chiptune, new CallbackHandler() {
            @Override
            public void onHandle(Object value) {
                Timber.i("Upvote Successful [%s, %s]", chiptune.title, chiptune.id);
            }

            @Override
            public void onFailure(String msg) {
                Timber.e("Error upvoting chiptune: %s", msg);
            }
        });
    }

    @Override
    public void downvoteChiptune(final Chiptune chiptune) {
        mVoteManager.downvote(chiptune, new CallbackHandler() {
            @Override
            public void onHandle(Object value) {
                Timber.i("Downvote Successful [%s, %s]", chiptune.title, chiptune.id);
            }

            @Override
            public void onFailure(String msg) {
                Timber.e("Error downvoting chiptune: %s", msg);
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
                if(msg != null) {
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
     * Apply a set of chiptunes to the UI
     *
     * @param chiptunes
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
        mView.hideProgress();
    }
}
