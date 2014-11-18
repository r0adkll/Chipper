package com.r0adkll.chipper.core.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.data.sync
 * Created by drew.heavner on 11/18/14.
 */
public class SyncService extends Service {

    @Inject
    SyncAdapter mSyncAdapter;

    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
