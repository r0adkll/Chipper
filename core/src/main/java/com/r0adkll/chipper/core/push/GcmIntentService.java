package com.r0adkll.chipper.core.push;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

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

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()){

            switch (messageType){
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:

                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:

                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:

                    // Parse message details from the extras



                    break;
            }

        }

        // Release the wake lock provided by WakefulBroadcastReceiver
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
