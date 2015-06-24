package com.r0adkll.chipper.ui;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.qualifiers.FlexSpaceHeader;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.qualifiers.GenericPrefs;
import com.r0adkll.chipper.qualifiers.OfflineSwitchPreference;
import com.r0adkll.chipper.ui.settings.SettingsModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui
 * Created by drew.heavner on 11/12/14.
 */
@Module(
    includes = SettingsModule.class,
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

    @Provides @FlexSpaceHeader
    View provideFlexibleSpaceHeader(Application app){
        View headerView = new View(app);

        // Get Headerspace
        int headerHeight = app.getResources().getDimensionPixelSize(R.dimen.actionbar_extended_height);
        headerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight));

        return headerView;
    }

}
