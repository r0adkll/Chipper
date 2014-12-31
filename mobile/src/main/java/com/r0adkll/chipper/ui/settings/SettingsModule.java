package com.r0adkll.chipper.ui.settings;

import android.content.SharedPreferences;

import com.r0adkll.chipper.qualifiers.DefaultPrefs;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.utils.prefs.StringPreference;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.settings
 * Created by drew.heavner on 12/9/14.
 */
@Module(
    injects = {
        SettingsActivity.class,
        SettingsActivity.GeneralSettings.class,
        SettingsActivity.DownloadingSettings.class,
        SettingsActivity.AccountSettings.class,
        SettingsActivity.AboutSettings.class
    },
    complete = false,
    library = true
)
public class SettingsModule {

    @Provides @Singleton
    @Named("OfflineWifiOnly")
    BooleanPreference provideOfflineOnlyPreference(@DefaultPrefs SharedPreferences prefs){
        return new BooleanPreference(prefs, "pref_offline_wifi_only", true);
    }

    @Provides @Singleton
    @Named("CacheDuringPlayback")
    BooleanPreference provideCacheDuringPlaybackPreference(@DefaultPrefs SharedPreferences prefs){
        return new BooleanPreference(prefs, "pref_cache_during_playback", true);
    }

    @Provides @Singleton
    @Named("AutoUpvote")
    BooleanPreference provideAutoUpvotePreference(@DefaultPrefs SharedPreferences prefs){
        return new BooleanPreference(prefs, "pref_auto_upvote", true);
    }

    @Provides @Singleton
    @Named("AutoSkip")
    BooleanPreference provideAutoSkipPreference(@DefaultPrefs SharedPreferences prefs){
        return new BooleanPreference(prefs, "pref_auto_skip", true);
    }

    @Provides @Singleton
    @Named("ShowOnlyVoted")
    BooleanPreference provideShowOnlyVotedPreference(@DefaultPrefs SharedPreferences prefs){
        return new BooleanPreference(prefs, "pref_show_only_voted", true);
    }

    @Provides @Singleton
    @Named("AutoScroll")
    BooleanPreference provideAutoScrollPreference(@DefaultPrefs SharedPreferences prefs){
        return new BooleanPreference(prefs, "pref_auto_scroll", true);
    }

    @Provides @Singleton
    @Named("Theme")
    StringPreference provideThemePreference(@DefaultPrefs SharedPreferences prefs){
        return new StringPreference(prefs, "pref_theme_picker", "0");
    }

    @Provides @Singleton
    @Named("DashboardNumMostPlayed")
    StringPreference provideNumMostPlayedPreference(@DefaultPrefs SharedPreferences prefs){
        return new StringPreference(prefs, "pref_num_most_played", "0");
    }



}
