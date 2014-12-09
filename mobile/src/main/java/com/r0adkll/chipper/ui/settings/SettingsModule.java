package com.r0adkll.chipper.ui.settings;

import dagger.Module;

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



}
