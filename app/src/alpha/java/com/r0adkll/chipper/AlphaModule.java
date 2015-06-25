package com.r0adkll.chipper;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.utils.qualifiers.Flavor;
import com.r0adkll.chipper.utils.qualifiers.Sauce;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ollie.Ollie;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;

/**
 * Created by r0adkll on 6/25/15.
 */
@Module
public class AlphaModule {

    private static final String PRODUCTION_BASE_URL = "http://r0adkll.com:6080/CHIPPER/V1/";

    @Provides @Singleton @Sauce
    String provideSauce(){
        return "9tCvAu3ZVs43FPWt0MFcLGJ/d1tKzHsXTLWKjC2eM0Y=";
    }

    @Provides @Singleton @Flavor
    String provideFlavor(){
        return "TShNKJza+E12/+1Wn+/O4qKwYlgeudZyXN0ddpMV6hA=";
    }

    @Provides @Singleton
    Ollie.LogLevel provideOllieLogLevel(){
        return Ollie.LogLevel.BASIC;
    }

    @Provides @Singleton
    RestAdapter.LogLevel provideRetrofitLogLevel(){
        return RestAdapter.LogLevel.FULL;
    }

    @Provides @Singleton
    Endpoint provideEndpoint(){
        return Endpoints.newFixedEndpoint(PRODUCTION_BASE_URL);
    }

    @Provides @Singleton
    ChipperService provideChipperService(RestAdapter restAdapter) {
        return restAdapter.create(ChipperService.class);
    }

}
