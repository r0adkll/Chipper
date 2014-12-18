package com.r0adkll.chipper.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.r0adkll.chipper.ChipperApp;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.data.sync
 * Created by drew.heavner on 11/18/14.
 */
public class SyncService extends Service {

    private static SyncAdapter mSyncAdapter;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock){
            if(mSyncAdapter == null){
                mSyncAdapter = new SyncAdapter(this);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
