package com.r0adkll.chipper.core.data;

import android.os.AsyncTask;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.r0adkll.chipper.core.api.ApiModule;
import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.qualifiers.CurrentUser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * This class will be used to inject a easy helper that dynamically loads the master list of
 * chiptunes in this order:
 *
 * • Memory
 * • Disk
 * • Server
 *
 * Created by r0adkll on 11/16/14.
 */
@Singleton
public class ChiptuneProvider {

    /**
     * The list of chiptunes to provide to the UI
     */
    private List<Chiptune> mChiptunes;

    private ChipperService mService;
    private User mCurrentUser;

    /**
     * Constructor
     * @param service
     * @param user
     */
    @Inject
    public ChiptuneProvider(ChipperService service, @CurrentUser User user){
        mChiptunes = new ArrayList<>();
        mService = service;
        mCurrentUser = user;
    }

    /**
     * Load all the chiptunes either via Database or by the API
     *
     * @param cb            the callback
     */
    public void loadChiptunes(final Callback<List<Chiptune>> cb){
        final long start = System.currentTimeMillis();

        // First check for memory cache
        if(!mChiptunes.isEmpty()){
            cb.success(mChiptunes, null);
            return;
        }

        // Second attempt to load from database
        new AsyncTask<Void, Void, List<Chiptune>>(){
            @Override
            protected List<Chiptune> doInBackground(Void... params) {

                // Attempt to load existing chiptunes
                List<Chiptune> chiptunes = new Select()
                        .from(Chiptune.class)
                        .execute();

                if(chiptunes != null && !chiptunes.isEmpty()) {
                    Timber.i("Chiptunes loaded from database in %d ms", System.currentTimeMillis() - start);
                    mChiptunes = new ArrayList<>(chiptunes);
                    return chiptunes;
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<Chiptune> chiptunes) {
                if(chiptunes != null){
                    cb.success(chiptunes, null);
                }else{
                    if (mCurrentUser != null) {

                        // Form the request auth header
                        String auth = ApiModule.generateAuthParam(mCurrentUser);

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

                                Timber.i("Chiptunes loaded from API: %d", chiptunes.size());
                                mChiptunes = new ArrayList<>(chiptunes);
                                cb.success(chiptunes, response);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                cb.failure(error);
                            }
                        });

                    }
                }
            }
        }.execute();
    }

}
