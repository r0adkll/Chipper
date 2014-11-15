package com.r0adkll.chipper.ui.popular;

import android.content.Intent;

import com.r0adkll.chipper.core.api.ApiModule;
import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.ChipperError;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.data.OfflineIntentService;
import com.r0adkll.chipper.core.data.model.OfflineRequest;
import com.r0adkll.chipper.core.utils.ChiptuneComparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    /**
     * Constructor
     * @param view      the view interface
     * @param service   the api service
     */
    public PopularPresenterImpl(PopularView view, ChipperService service, User user){
        mView = view;
        mService = service;
        mCurrentUser = user;
    }


    @Override
    public void loadAllChiptunes() {
        ApiModule.loadChiptunes(mCurrentUser, mService, new Callback<List<Chiptune>>() {
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

        String auth = ApiModule.generateAuthParam(mCurrentUser);
        mService.getVotes(auth, new Callback() {
            @Override
            public void success(Object o, Response response) {

                // Set the vote data in the view
                mView.setVoteData();
            }

            @Override
            public void failure(RetrofitError error) {
                handleRetrofitError(error);
            }
        });

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

        OfflineRequest request = new OfflineRequest.Builder()
                .addChiptunes(Arrays.asList(chiptunes))
                .build();

        Intent offlineRequest = new Intent(mView.getActivity(), OfflineIntentService.class);
        offlineRequest.putExtra(OfflineIntentService.EXTRA_OFFLINE_REQUEST, request);
        mView.getActivity().startService(offlineRequest);

    }


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
        ChipperError cer = (ChipperError) error.getBodyAs(ChipperError.class);
        if(cer != null){
            Timber.e("Retrofit Error[%s] - %s", error.getMessage(), cer.technical);
            mView.showErrorMessage(cer.readable);
        }else{
            Timber.e("Retrofit Error: %s", error.getKind().toString());
            mView.showErrorMessage(error.getLocalizedMessage());
        }
    }
}
