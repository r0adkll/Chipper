package com.r0adkll.chipper.push;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.push.model.PushNode;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.push
 * Created by drew.heavner on 11/18/14.
 */
public class GcmIntentService extends IntentService{

    @Inject
    PushNode mNode;

    /**
     * Constructor
     */
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ChipperApp.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()){

            switch (messageType){
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    mNode.onPushSendError(extras);
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    mNode.onPushDeleted(extras);
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    String type = extras.getString("type", "");
                    mNode.onPushMessage(type, extras);
                    break;
            }

        }

        // Release the wake lock provided by WakefulBroadcastReceiver
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
