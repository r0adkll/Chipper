package com.r0adkll.chipper.core.push;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.r0adkll.chipper.core.prefs.StringPreference;
import com.r0adkll.chipper.core.qualifiers.GenericPrefs;
import com.r0adkll.chipper.core.qualifiers.PushToken;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.push
 * Created by drew.heavner on 11/18/14.
 */
@Module(
    injects = (
        PushManager.class
    ),
    complete = false,
    library = true
)
public class PushModule {
    private static final String PREF_PUSH_TOKEN = "pref_push_token";

    @Provides
    GoogleCloudMessaging provideCloudMessaging(Application app){
        return GoogleCloudMessaging.getInstance(app);
    }

    @Provides @Singleton @PushToken
    StringPreference providePushTokenPreference(@GenericPrefs SharedPreferences prefs){
        return new StringPreference(prefs, PREF_PUSH_TOKEN, "");
    }

}
