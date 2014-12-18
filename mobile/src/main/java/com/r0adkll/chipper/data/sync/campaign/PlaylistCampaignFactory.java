package com.r0adkll.chipper.data.sync.campaign;

import com.r0adkll.chipper.api.ChipperService;
import com.squareup.otto.Bus;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.data.sync.campaign
 * Created by drew.heavner on 12/18/14.
 */
public class PlaylistCampaignFactory implements CampaignFactory {

    private ChipperService mService;
    private Bus mBus;

    /**
     * Constructor
     *
     * @param service
     * @param bus
     */
    public PlaylistCampaignFactory(ChipperService service, Bus bus){
        mService = service;
        mBus = bus;
    }

    @Override
    public SyncCampaign createCampaign() {
        return new PlaylistCampaign(mService, mBus);
    }

}
