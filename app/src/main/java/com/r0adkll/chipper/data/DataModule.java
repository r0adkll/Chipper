package com.r0adkll.chipper.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ftinc.kit.preferences.SecurePreferences;
import com.r0adkll.chipper.BuildConfig;
import com.r0adkll.chipper.utils.Tools;
import com.r0adkll.chipper.utils.qualifiers.DeviceId;
import com.r0adkll.chipper.utils.qualifiers.Flavor;
import com.r0adkll.chipper.utils.qualifiers.Sauce;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 6/26/15.
 */
@Module
public class DataModule {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    private static final String SECURE_PREF_NAME = BuildConfig.APPLICATION_ID.concat(".secure");

    /***********************************************************************************************
     *
     * Preferences
     *
     */

    @Provides @Singleton
    SharedPreferences provideDefaultSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides @Singleton
    SecurePreferences provideDefaultSecurePreferences(Context context,
                                                      @Sauce String sauce,
                                                      @Flavor String flavor){
        return new SecurePreferences(context, SECURE_PREF_NAME, sauce, flavor, true);
    }




    @Provides @Singleton @DeviceId
    String provideDeviceId(Application app){
        return Tools.generateUniqueDeviceId(app);
    }

}
