package com.r0adkll.chipper.core;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.r0adkll.chipper.core.api.ApiModule;
import com.r0adkll.chipper.core.data.DataModule;
import com.r0adkll.chipper.core.data.OfflineIntentService;
import com.r0adkll.chipper.core.prefs.IntPreference;
import com.r0adkll.chipper.core.push.PushModule;
import com.r0adkll.chipper.core.qualifiers.AppVersion;
import com.r0adkll.chipper.core.qualifiers.DefaultPrefs;
import com.r0adkll.chipper.core.qualifiers.GenericPrefs;
import com.r0adkll.deadskunk.utils.SecurePreferences;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by r0adkll on 11/10/14.
 */
@Module(
    injects = {
        OfflineIntentService.class
    },
    includes = {
        ApiModule.class,
        DataModule.class,
        PushModule.class
    },
    complete = false,
    library = true
)
public class CoreModule {
    private static final String SAUCE = "sE2s3KWwQGEf3cqXUyZmdd1vOmsMoirDwFxhfMOrUac=";
    private static final String FLAVOR = "ijuiqkljaisudfijeknxnxnxmsnjkiufwkj";
    private static final String SECURE_PREFERENCE_NAME = "secure.prefs";
    private static final String GENERIC_PREFERENCE_NAME = "generic.prefs";

    private static final String PREF_APP_VERSION = "pref_app_version";

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return new OkHttpClient();
    }

    @Provides @Singleton @GenericPrefs
    SharedPreferences provideGenericPreferences(Application app) {
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



}
