package com.r0adkll.chipper.api;

import android.app.Application;

import com.google.gson.Gson;
import com.r0adkll.chipper.data.model.Device;
import com.r0adkll.chipper.data.model.User;
import com.r0adkll.chipper.utils.TimberLog;
import com.r0adkll.chipper.utils.Tools;
import com.squareup.okhttp.OkHttpClient;

import java.util.LinkedHashMap;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * This module defines all the injectable components that will be used
 * for interfacing with the new Chipper JAVA api.
 *
 * Created by r0adkll on 11/10/14.
 */
@Module
public final class ApiModule {

    @Provides @Singleton
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides @Singleton
    ChipperErrorHandler provideErrorHandler(Application app){
        return new ChipperErrorHandler(app);
    }

    @Provides @Singleton
    RestAdapter provideRestAdapter(Endpoint endpoint,
                                   Client client,
                                   LogLevel logLevel,
                                   ApiHeaders headers,
                                   ChipperErrorHandler errorHandler) {
        return new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(endpoint)
                .setRequestInterceptor(headers)
                .setErrorHandler(errorHandler)
                .setLog(new TimberLog())
                .setLogLevel(logLevel)
                .setConverter(new GsonConverter(new Gson()))
                .build();
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
    public static String generateDeviceAuthParam(Gson gson, User user, Device device){
        LinkedHashMap<String, Object> auth = new LinkedHashMap<>();

        // Add auth params
        try {
            auth.put("user_id", user.id);
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
