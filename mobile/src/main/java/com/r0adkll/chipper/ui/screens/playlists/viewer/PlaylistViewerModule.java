package com.r0adkll.chipper.ui.screens.playlists.viewer;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.ui.UIModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 11/16/14.
 */
@Module(
    injects = PlaylistViewerActivity.class,
    addsTo = UIModule.class,
    complete = false,
    library = true
)
public class PlaylistViewerModule {
    private PlaylistViewerView view;
    public PlaylistViewerModule(PlaylistViewerView view){
        this.view = view;
    }

    @Provides @Singleton
    PlaylistViewerView provideView(){
        return view;
    }

    @Provides @Singleton
    PlaylistViewerPresenter providePresenter(PlaylistViewerView view,
                                             ChipperService service,
                                             PlaylistManager playlistManager,
                                             VoteManager voteManager,
                                             ChiptuneProvider provider,
                                             @CurrentUser User user){
        return new PlaylistViewerPresenterImpl(view, service, playlistManager, voteManager, provider, user);
    }

}
