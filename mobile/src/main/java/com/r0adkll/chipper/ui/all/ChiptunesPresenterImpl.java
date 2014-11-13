package com.r0adkll.chipper.ui.all;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.ChipperError;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.utils.Tools;

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

    private ChiptunesView mView;
    private ChipperService mService;

    /**
     * Constructor
     *
     * @param view          the chipper view interface
     * @param service       the chipper API service
     */
    public ChiptunesPresenterImpl(ChiptunesView view, ChipperService service){
        mView = view;
        mService = service;
    }

    @Override
    public void loadAllChiptunes() {
        mView.showProgress();

        // Attempt to load existing chiptunes
        List<Chiptune> chiptunes = new Select()
                .from(Chiptune.class)
                .execute();

        if(chiptunes == null) {

            // Load the current user
            User currentUser = new Select()
                    .from(User.class)
                    .limit(1)
                    .executeSingle();

            if (currentUser != null) {
                // Form the request auth header
                String auth = generateAuthParam(currentUser);

                // Make request
                mService.getChiptunes(auth, new Callback<List<Chiptune>>() {
                    @Override
                    public void success(List<Chiptune> chiptunes, Response response) {

                        // Save all the chiptunes
                        ActiveAndroid.beginTransaction();
                        try{
                            for(Chiptune chiptune: chiptunes){
                                chiptune.save();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } finally{
                            ActiveAndroid.endTransaction();
                        }

                        mView.hideProgress();
                        mView.setChiptunes(chiptunes);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mView.hideProgress();
                        handleRetrofitError(error);
                    }
                });

            }

        }else{
            mView.hideProgress();
            mView.setChiptunes(chiptunes);
        }
    }

    @Override
    public void onChiptuneSelected(Chiptune chiptune) {
        // Send Otto Event to start playing this selected chiptune

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

    /**
     * Generate the auth params and keystore hash
     * @return
     */
    public static String generateAuthParam(User user){
        Map<String, Object> auth = new HashMap<>();

        // Add auth params
        auth.put("user_id", user.id);
        auth.put("public_key", user.public_key);
        auth.put("timestamp", System.currentTimeMillis()/1000);

        // Now generate the hash
        Gson gson = new Gson();
        String params = gson.toJson(auth);
        String hash = Tools.sha256(params.concat(user.private_key));
        auth.put("hash", hash);

        return gson.toJson(auth);
    }


}
