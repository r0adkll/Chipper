package com.r0adkll.chipper.tv.ui.leanback.browse;

import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.tv.ui.TVUiModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 12/7/14.
 */
@Module(
    injects = ChipperBrowseFragment.class,
    addsTo = TVUiModule.class,
    complete = false
)
public class BrowseModule {

    private BrowseView view;

    public BrowseModule(BrowseView view){
        this.view = view;
    }

    @Provides @Singleton
    BrowseView provideView(){
        return view;
    }

    @Provides @Singleton
    BrowsePresenter providePresenter(BrowseView view,
                                     @CurrentUser User user,
                                     ChiptuneProvider provider,
                                     PlaylistManager playlistManager){
        return new BrowsePresenterImpl(view, user, provider, playlistManager);
    }

}
