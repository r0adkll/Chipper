package com.r0adkll.chipper;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.r0adkll.chipper.api.ApiModule;
import com.r0adkll.chipper.data.DataModule;
import com.r0adkll.chipper.playback.PlaybackModule;
import com.r0adkll.chipper.prefs.IntPreference;
import com.r0adkll.chipper.push.PushModule;
import com.r0adkll.chipper.qualifiers.AppVersion;
import com.r0adkll.chipper.qualifiers.DefaultPrefs;
import com.r0adkll.chipper.qualifiers.GenericPrefs;
import com.r0adkll.chipper.ui.UIModule;
import com.r0adkll.deadskunk.utils.SecurePreferences;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper
 * Created by drew.heavner on 11/12/14.
 */
@Module(
    includes = {
        ApiModule.class,
        DataModule.class,
        PushModule.class,
        UIModule.class,
        PlaybackModule.class
    },
    injects = {
        ChipperApp.class
    },
    library = true
)
public final class ChipperModule {
    private static final String SAUCE = "sE2s3KWwQGEf3cqXUyZmdd1vOmsMoirDwFxhfMOrUac=";
    private static final String FLAVOR = "ijuiqkljaisudfijeknxnxnxmsnjkiufwkj";
    private static final String SECURE_PREFERENCE_NAME = "secure.prefs";
    private static final String GENERIC_PREFERENCE_NAME = "generic.prefs";
    private static final String PREF_APP_VERSION = "pref_app_version";

    private final ChipperApp app;

    /**
     * Constructor
     */
    public ChipperModule(ChipperApp app){
        this.app = app;
    }

    @Provides @Singleton
    Application provideApplication(){
        return app;
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides @Singleton @GenericPrefs
    SharedPreferences provideGenericPreferences() {
        return app.getSharedPreferences(GENERIC_PREFERENCE_NAME, MODE_PRIVATE);
    }

    @Provides @Singleton @DefaultPrefs
    SharedPreferences provideDefaultPreferences(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides @Singleton
    SecurePreferences provideSecurePreferences(Application app){
        return new SecurePreferences(app, SECURE_PREFERENCE_NAME, SAUCE, FLAVOR, true);
    }

    @Provides @Singleton @AppVersion
    IntPreference provideAppVersionPreference(@GenericPrefs SharedPreferences prefs){
        return new IntPreference(prefs, PREF_APP_VERSION, Integer.MIN_VALUE);
    }

    @Provides @Singleton
    Gson provideGson(){
        return new Gson();
    }

    @Provides @Singleton
    Bus provideOttoBus(){
        return new Bus(ThreadEnforcer.ANY);
    }

    /***********************************************************************************************
     *
     * System Service Provides
     *
     */

    @Provides
    NotificationManagerCompat provideNotificationManager(Application app){
        return NotificationManagerCompat.from(app);
    }



}