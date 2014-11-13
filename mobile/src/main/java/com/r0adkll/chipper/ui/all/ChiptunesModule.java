package com.r0adkll.chipper.ui.all;

import com.r0adkll.chipper.ui.UIModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 11/13/14.
 */
@Module(
    injects = ChiptunesActivity.class,
    addsTo = UIModule.class,
    complete = false
)
public class ChiptunesModule {
    private ChiptunesView view;

    public ChiptunesModule(ChiptunesView view){
        this.view = view;
    }

    @Provides @Singleton
    ChiptunesView provideView(){
        return view;
    }

    @Provides @Singleton
    ChiptunesPresenter providePresenter(){
        return new ChiptunesPresenterImpl();
    }

}
