package com.r0adkll.chipper.api;

import com.google.gson.Gson;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.utils.Tools;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

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
    RestAdapter provideRestAdapter(Endpoint endpoint, Client client, ApiHeaders headers) {
        return new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(endpoint)
                .setRequestInterceptor(headers)
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
    public static String generateUserAuthParam(Gson gson, User user){
        LinkedHashMap<String, Object> auth = new LinkedHashMap<>();

        // Add auth params
        try {
            auth.put("user_id", user.id);
            auth.put("public_key", user.public_key);
            auth.put("timestamp", Tools.time());

            String stage1Raw = gson.toJson(auth);
            String compose = stage1Raw.concat(user.private_key);

            // Now generate the hash
            String hash = Tools.sha256(compose);
            auth.put("hash", hash);

            return gson.toJson(auth);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Generate the auth params and keystore hash
     * @return
     */
    public static String generateDeviceAuthParam(Gson gson, Device device){
        LinkedHashMap<String, Object> auth = new LinkedHashMap<>();

        // Add auth params
        try {
            auth.put("device_id", device.id);
            auth.put("public_key", device.public_key);
            auth.put("timestamp", Tools.time());

            String stage1Raw = gson.toJson(auth);
            String compose = stage1Raw.concat(device.private_key);

            // Now generate the hash
            String hash = Tools.sha256(compose);
            auth.put("hash", hash);

            return gson.toJson(auth);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return "";
    }

}
