package com.r0adkll.chipper.push;

import android.accounts.Account;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.core.CoreApplication;
import com.r0adkll.chipper.qualifiers.CurrentUser;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.push
 * Created by drew.heavner on 11/18/14.
 */
public class GcmIntentService extends IntentService{

    public static final String AUTHORITY = "com.r0adkll.chipper.provider";
    public static final String ACCOUNT_TYPE = "r0adkll.com";
    public static final String KEY_SYNC_REQUEST = "com.r0adkll.chipper.SYNC";


    @Inject @CurrentUser
    User mCurrentUser;

    /**
     * Constructor
     */
    public GcmIntentService() {
        super("GcmIntentService");
        CoreApplication.get(this).inject(this);
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
                    String type = extras.getString("type", "");
                    switch (type){
                        case KEY_SYNC_REQUEST:

                            if(mCurrentUser != null) {

                                // Generate account used for the sync adapter
                                Account acct = new Account(mCurrentUser.email, ACCOUNT_TYPE);

                                // Sync push received, cause a sync to occur
                                ContentResolver.requestSync(acct, AUTHORITY, null);

                            }
                            break;
                    }
                    break;
            }

        }

        // Release the wake lock provided by WakefulBroadcastReceiver
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
