package com.r0adkll.chipper.tv.ui.leanback.playback;

import com.r0adkll.chipper.tv.ui.TVUiModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.tv.ui.leanback.playlist
 * Created by drew.heavner on 12/8/14.
 */
@Module(
    injects = TVPlaybackFragment.class,
    addsTo = TVUiModule.class,
    complete = false
)
public class TVPlaybackModule {

    private TVPlaybackView view;

    /**
     * Constructor
     *
     * @param view      the view interface
     */
    public TVPlaybackModule(TVPlaybackView view){
        this.view = view;
    }

    @Provides @Singleton
    TVPlaybackView provideView(){
        return view;
    }

    @Provides @Singleton
    TVPlaybackPresenter providePresenter(TVPlaybackView view){
        return new TVPlaybackPresenterImpl(view);
    }

}
