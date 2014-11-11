package com.r0adkll.chipper.core.api;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

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
                .build();
    }

    @Provides @Singleton
    ChipperService provideChipperService(RestAdapter restAdapter) {
        return restAdapter.create(ChipperService.class);
    }

}
