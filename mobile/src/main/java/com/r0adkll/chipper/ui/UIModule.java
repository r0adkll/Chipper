package com.r0adkll.chipper.ui;

import android.content.SharedPreferences;

import com.r0adkll.chipper.prefs.BooleanPreference;
import com.r0adkll.chipper.qualifiers.GenericPrefs;
import com.r0adkll.chipper.qualifiers.OfflineSwitchPreference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui
 * Created by drew.heavner on 11/12/14.
 */
@Module(
    injects = {
        Chipper.class
    },
    complete = false,
    library = true
)
public class UIModule {

    @Provides @Singleton @OfflineSwitchPreference
    BooleanPreference provideOfflineSwitchPreference(@GenericPrefs SharedPreferences prefs){
        return new BooleanPreference(prefs, "pref_offline_mode", false);
    }

}
