package com.r0adkll.chipper.data.sync.campaign;

import com.r0adkll.chipper.api.ChipperService;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.data.sync.campaign
 * Created by drew.heavner on 12/18/14.
 */
public interface CampaignFactory {
    public SyncCampaign createCampaign();
}
