package com.r0adkll.chipper.push;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.push.model.ProductionPushNode;
import com.r0adkll.chipper.push.model.PushNode;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.utils.prefs.StringPreference;
import com.r0adkll.chipper.qualifiers.GenericPrefs;
import com.r0adkll.chipper.qualifiers.PushToken;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.push
 * Created by drew.heavner on 11/18/14.
 */
@Module(
    injects = {
        PushManager.class,
        GcmIntentService.class
    },
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

    @Provides @Singleton
    PushNode providePushNode(Application app,
                             @CurrentUser User user,
                             NotificationManagerCompat notifMan){
        return new ProductionPushNode(app, user, notifMan);
    }

}
