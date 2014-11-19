package com.r0adkll.chipper.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.data.sync
 * Created by drew.heavner on 11/18/14.
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
