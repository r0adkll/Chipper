package com.r0adkll.chipper.tv.ui.login;

import android.content.SharedPreferences;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.qualifiers.GenericPrefs;
import com.r0adkll.chipper.tv.ui.TVUiModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 12/7/14.
 */
@Module(
    injects = TVLoginActivity.class,
    addsTo = TVUiModule.class,
    complete = false
)
public class TVLoginModule {

    private TVLoginView view;

    /**
     * Constructor
     */
    public TVLoginModule(TVLoginView view){
        this.view = view;
    }

    @Provides @Singleton
    TVLoginView provideView(){
        return view;
    }

    @Provides @Singleton
    TVLoginPresenter providePresenter(TVLoginView view,
                                      ChipperService service,
                                      @GenericPrefs SharedPreferences prefs){
        return new TVLoginPresenterImpl(view, service, prefs);
    }

}
