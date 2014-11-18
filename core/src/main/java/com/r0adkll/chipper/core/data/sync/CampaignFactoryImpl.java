package com.r0adkll.chipper.core.data.sync;

import android.content.SyncResult;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.data.sync
 * Created by drew.heavner on 11/18/14.
 */
public class CampaignFactoryImpl implements SyncCampaign.Factory {

    public CampaignFactoryImpl(){}

    @Override
    public SyncCampaign create(SyncResult syncResult) {
        return new SyncCampaign(syncResult);
    }
}
