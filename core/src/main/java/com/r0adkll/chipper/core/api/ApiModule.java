package com.r0adkll.chipper.core.api;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.utils.Tools;
import com.squareup.okhttp.OkHttpClient;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Callback;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

/**
 * This module defines all the injectable components that will be used
 * for interfacing with the new Chipper JAVA api.
 *
 * Created by r0adkll on 11/10/14.
 */
@Module(
    complete = false,
    library = true
)
public final class ApiModule {

    public static final String PRODUCTION_BASE_URL = "http://r0adkll.com:6080/CHIPPER/V1/";

    @Provides @Singleton
    Endpoint provideEndpoint(){
        return Endpoints.newFixedEndpoint(PRODUCTION_BASE_URL);
    }

    @Provides @Singleton
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides @Singleton
    RestAdapter provideRestAdapter(Endpoint endpoint, Client client) {
        return new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(endpoint)
                .setConverter(new GsonConverter(new Gson()))
                .build();
    }

    @Provides @Singleton
    ChipperService provideChipperService(RestAdapter restAdapter) {
        return restAdapter.create(ChipperService.class);
    }

    /**
     * Generate the auth params and keystore hash
     * @return
     */
    public static String generateAuthParam(User user){
        JSONObject auth = new JSONObject();

        // Add auth params
        try {

            auth.put("public_key", user.public_key);
            auth.put("user_id", user.id);
            auth.put("timestamp", System.currentTimeMillis()/1000L);

            String compose = auth.toString().concat(user.private_key);

            Timber.i("Compose pre-hash: %s", compose);

            // Now generate the hash
            String hash = Tools.sha256(compose);
            auth.put("hash", hash);

            Timber.i("Post-Hash: %s", hash);

            return auth.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Load all the chiptunes either via Database or by the API
     *
     * @param user          the current user to auth with
     * @param service       the chipper api service
     * @param cb            the callback
     */
    public static void loadChiptunes(User user, ChipperService service, final Callback<List<Chiptune>> cb){

        // Attempt to load existing chiptunes
        List<Chiptune> chiptunes = new Select()
                .from(Chiptune.class)
                .execute();

        if(chiptunes == null || chiptunes.isEmpty()) {

            if (user != null) {

                // Form the request auth header
                String auth = ApiModule.generateAuthParam(user);

                // Make request
                service.getChiptunes(auth, new Callback<List<Chiptune>>() {
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
                        cb.success(chiptunes, response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        cb.failure(error);
                    }
                });

            }

        }else{
            Timber.i("Chiptunes loaded from database: %d", chiptunes.size());
            cb.success(chiptunes, null);
        }
    }


}
