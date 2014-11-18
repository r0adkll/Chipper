package com.r0adkll.chipper.core.push;

import android.app.IntentService;
import android.content.Intent;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.push
 * Created by drew.heavner on 11/18/14.
 */
public class GcmIntentService extends IntentService{


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
