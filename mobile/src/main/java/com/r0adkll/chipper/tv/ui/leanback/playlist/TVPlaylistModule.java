package com.r0adkll.chipper.tv.ui.leanback.playlist;

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
    injects = TVPlaylistActivity.class,
    addsTo = TVUiModule.class,
    complete = false
)
public class TVPlaylistModule {

    private TVPlaylistView view;

    /**
     * Constructor
     *
     * @param view      the view interface
     */
    public TVPlaylistModule(TVPlaylistView view){
        this.view = view;
    }

    @Provides @Singleton
    TVPlaylistView provideView(){
        return view;
    }

    @Provides @Singleton
    TVPlaylistPresenter providePresenter(TVPlaylistView view){
        return new TVPlaylistPresenterImpl(view);
    }

}
