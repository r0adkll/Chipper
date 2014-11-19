package com.r0adkll.chipper.data.sync;

import android.content.SyncResult;

import com.r0adkll.chipper.api.ChipperService;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.data.sync
 * Created by drew.heavner on 11/18/14.
 */
public class CampaignFactoryImpl implements SyncCampaign.Factory {

    public CampaignFactoryImpl(){}

    @Override
    public SyncCampaign create(ChipperService service, SyncResult syncResult) {
        return new SyncCampaign(service, syncResult);
    }
}
