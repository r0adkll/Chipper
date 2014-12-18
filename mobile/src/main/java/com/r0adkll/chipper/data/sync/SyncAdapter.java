package com.r0adkll.chipper.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.r0adkll.chipper.BuildConfig;
import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.api.ChipperService;

import javax.inject.Inject;

import timber.log.Timber;

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

        if (!BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    Timber.e("Uncaught sync exception, suppressing UI in release build.", throwable);
                }
            });
        }

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        // Cancel existing campaigns
        cancelCampaign();

        // Start/Create new campaign
        mCampaign = mCampaignFactory.create(mService, syncResult);

        // Run the campaign
        try {
            mCampaign.run();
        }catch(Exception e){
            Timber.e(e, "Uncaught error occured on on the Sync Campaign");
        }

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
