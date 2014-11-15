package com.r0adkll.chipper.ui.all;

import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.r0adkll.chipper.core.api.ApiModule;
import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.ChipperError;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.data.OfflineIntentService;
import com.r0adkll.chipper.core.data.model.OfflineRequest;
import com.r0adkll.chipper.core.utils.ChiptuneComparator;
import com.r0adkll.chipper.core.utils.Tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Constructor
     *
     * @param view          the chipper view interface
     * @param service       the chipper API service
     */
    public ChiptunesPresenterImpl(ChiptunesView view, ChipperService service, User user){
        mCurrentUser = user;
        mView = view;
        mService = service;
    }

    @Override
    public void loadAllChiptunes() {
        mView.showProgress();
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
    public void onChiptuneSelected(Chiptune chiptune) {
        // Send Otto Event to start playing this selected chiptune
        Timber.i("Chiptune selected[%s]: %s-%s", chiptune.id, chiptune.artist, chiptune.title);
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
