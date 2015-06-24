package com.r0adkll.chipper.data;

import android.os.AsyncTask;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.chipper.utils.ChiptuneComparator;
import com.r0adkll.deadskunk.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, Chiptune> mChiptuneMap;
    private ChipperService mService;
    private User mCurrentUser;

    /**
     * Constructor
     * @param service
     */
    @Inject
    public ChiptuneProvider(ChipperService service, User currentUser){
        mChiptunes = new ArrayList<>();
        mChiptuneMap = new HashMap<>();
        mService = service;
        mCurrentUser = currentUser;
    }

    /**
     * Get a chiptune for a specified id, or null
     * @param id
     * @return
     */
    public Chiptune getChiptune(String id){
        return mChiptuneMap.get(id);
    }

    /**
     * Get a random chiptune from the list of the chiptunes
     *
     * @return      get a random chiptune
     */
    public Chiptune getRandomChiptune(){
        int index = Utils.getRandom().nextInt(mChiptunes.size());
        return mChiptunes.get(index);
    }

    /**
     * Dynamically load a random chiptune. Instantly if the chiptunes are already loaded into memory
     * but load them from the db if not, and if not in the database, load them from the server
     *
     * @param cb        the callback
     */
    public void loadRandomChiptune(final CallbackHandler<Chiptune> cb){
        if(mChiptunes.size() > 0){
            cb.onHandle(getRandomChiptune());
        }else{
            loadChiptunes(new Callback<List<Chiptune>>() {
                @Override
                public void success(List<Chiptune> chiptunes, Response response) {
                    cb.onHandle(getRandomChiptune());
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.e(error, "Unable to get a random chiptune");
                }
            });
        }
    }

    /**
     * Return a new list of all chiptunes available
     *
     * @return      all teh chiptunes
     */
    public List<Chiptune> getAllChiptunes(){
        return new ArrayList<>(mChiptunes);
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
            if(cb != null) cb.success(mChiptunes, null);
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
                    setChiptunes(chiptunes);
                    return chiptunes;
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<Chiptune> chiptunes) {
                if(chiptunes != null){
                    if(cb != null) cb.success(chiptunes, null);
                }else{

                    // Make request
                    mService.getChiptunes(new Callback<List<Chiptune>>() {
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
                            setChiptunes(chiptunes);
                            if(cb != null) cb.success(chiptunes, response);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if(cb != null) cb.failure(error);
                        }
                    });


                }
            }
        }.execute();
    }

    /**
     * Set and hash the chiptunes
     * @param chiptunes
     */
    private void setChiptunes(List<Chiptune> chiptunes){
        mChiptunes = new ArrayList<>(chiptunes);
        mChiptuneMap = new HashMap<>();
        for(Chiptune chiptune:chiptunes){
            mChiptuneMap.put(chiptune.id, chiptune);
        }

        // Sort the chiptunes
        Collections.sort(mChiptunes, new ChiptuneComparator());

    }

}
