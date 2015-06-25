package com.r0adkll.chipper;

import com.r0adkll.chipper.api.ApiModule;
import com.r0adkll.chipper.ui.NewUiModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by r0adkll on 6/25/15.
 */
@Singleton
@Component(
    modules = {
        AppModule.class,
        AlphaModule.class,
        ApiModule.class,
        NewUiModule.class
    }
)
public interface AppComponent extends AppGraph{

    final class Initializer {
        static AppGraph init(ChipperApp app){
            return DaggerAppComponent.builder()
                    .appModule(new AppModule(app))
                    .alphaModule(new AlphaModule())
                    .apiModule(new ApiModule())
                    .uiModule(new NewUiModule())
                    .build();
        }
        private Initializer(){}
    }

}