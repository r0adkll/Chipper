package com.r0adkll.chipper.core.data.sync;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.data.sync
 * Created by drew.heavner on 11/18/14.
 */
@Module(
    injects = {
        SyncAdapter.class,
        SyncService.class
    },
    complete = false
)
public class SyncModule {

    @Provides
    @Singleton
    SyncCampaign.Factory provideSyncCampaignFactory(){
        return new CampaignFactoryImpl();
    }

    @Provides
    @Singleton
    SyncAdapter provideSyncAdapter(Application app){
        return new SyncAdapter(app);
    }

}
