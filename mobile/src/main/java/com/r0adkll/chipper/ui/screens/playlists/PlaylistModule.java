package com.r0adkll.chipper.ui.screens.playlists;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.ui.UIModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 11/16/14.
 */
@Module(
    injects = PlaylistActivity.class,
    addsTo = UIModule.class,
    complete = false,
    library = true
)
public class PlaylistModule {
    private PlaylistView view;
    public PlaylistModule(PlaylistView view){
        this.view = view;
    }

    @Provides @Singleton
    PlaylistView provideView(){
        return view;
    }

    @Provides @Singleton
    PlaylistPresenter providePresenter(PlaylistView view,
                                       ChipperService service,
                                       PlaylistManager manager,
                                       @CurrentUser User user){
        return new PlaylistPresenterImpl(view, service, manager, user);
    }

}
