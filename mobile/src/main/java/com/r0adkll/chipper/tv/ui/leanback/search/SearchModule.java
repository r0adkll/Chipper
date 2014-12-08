package com.r0adkll.chipper.tv.ui.leanback.search;

import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.tv.ui.TVUiModule;
import com.r0adkll.chipper.tv.ui.leanback.browse.ChipperBrowseFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 12/7/14.
 */
@Module(
    injects = ChipperSearchFragment.class,
    addsTo = TVUiModule.class,
    complete = false
)
public class SearchModule {

    private ChipperSearchView view;

    public SearchModule(ChipperSearchView view){
        this.view = view;
    }

    @Provides @Singleton
    ChipperSearchView provideView(){
        return view;
    }

    @Provides @Singleton
    SearchPresenter providePresenter(ChipperSearchView view,
                                     ChiptuneProvider provider,
                                     PlaylistManager playlistManager){
        return new SearchPresenterImpl(view, provider, playlistManager);
    }

}
