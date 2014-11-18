package com.r0adkll.chipper.core.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.data.sync
 * Created by drew.heavner on 11/18/14.
 */
@Singleton
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject
    SyncCampaign.Factory mCampaignFactory;

    private SyncCampaign mCampaign;

    /**
     * Dagger Constructor
     * @param context       context reference
     */
    @Inject
    public SyncAdapter(Context context) {
        super(context, true);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        // Cancel existing campaigns
        cancelCampaign();

        // Start/Create new campaign
        mCampaign = mCampaignFactory.create(syncResult);
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
