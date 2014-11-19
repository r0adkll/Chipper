package com.r0adkll.chipper.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.api.ChipperService;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.data.sync
 * Created by drew.heavner on 11/18/14.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject
    ChipperService mService;

    @Inject
    SyncCampaign.Factory mCampaignFactory;

    private SyncCampaign mCampaign;

    /**
     * Dagger Constructor
     * @param context       context reference
     */
    public SyncAdapter(Context context) {
        super(context, true);
        ChipperApp.get(context).inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        // Cancel existing campaigns
        cancelCampaign();

        // Start/Create new campaign
        mCampaign = mCampaignFactory.create(mService, syncResult);
        mCampaign.run();

    }

    @Override
    public void onSyncCanceled() {
        cancelCampaign();
    }

    /**
     * Cancel the current campaign
     */
    private void cancelCampaign(){
        if(mCampaign != null)
            mCampaign.cancel();
    }

}
