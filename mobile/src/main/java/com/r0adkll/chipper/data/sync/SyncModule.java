package com.r0adkll.chipper.data.sync;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.data.sync.campaign.CampaignFactory;
import com.r0adkll.chipper.data.sync.campaign.PlaylistCampaignFactory;
import com.r0adkll.chipper.qualifiers.PlaylistSyncFactory;
import com.squareup.otto.Bus;

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
        SyncAdapter.class
    },
    complete = false
)
public class SyncModule {

    @Provides
    @Singleton
    @PlaylistSyncFactory
    CampaignFactory providePlaylistCampaignFactory(ChipperService service, Bus bus){
        return new PlaylistCampaignFactory(service, bus);
    }

}