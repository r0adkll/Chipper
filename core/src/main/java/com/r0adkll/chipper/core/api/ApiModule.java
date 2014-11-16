package com.r0adkll.chipper.core.api;

import com.google.gson.Gson;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.utils.Tools;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

//import org.apache.commons.codec.digest.DigestUtils;

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

            // Now generate the hash
            String hash = Tools.sha256(compose);
            auth.put("hash", hash);

            return auth.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return "";
    }

}
