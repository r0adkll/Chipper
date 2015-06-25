package com.r0adkll.chipper;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 6/25/15.
 */
@Module
public class AppModule {

    private ChipperApp mApp;

    public AppModule(ChipperApp app) {
        this.mApp = app;
    }

    @Provides @Singleton
    Context provideApplicationContext(){
        return mApp;
    }

}
