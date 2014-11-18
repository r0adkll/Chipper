package com.r0adkll.chipper;

import android.app.Application;

import com.r0adkll.chipper.core.CoreModule;
import com.r0adkll.chipper.ui.UIModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper
 * Created by drew.heavner on 11/12/14.
 */
@Module(
    includes = {
        CoreModule.class,
        UIModule.class
    },
    injects = {
        ChipperApp.class
    }
)
public final class ChipperModule {

}
