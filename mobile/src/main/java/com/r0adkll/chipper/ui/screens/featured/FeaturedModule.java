package com.r0adkll.chipper.ui.screens.featured;

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
    injects = FeaturedActivity.class,
    addsTo = UIModule.class,
    complete = false,
    library = true
)
public class FeaturedModule {
    private FeaturedView view;
    public FeaturedModule(FeaturedView view){
        this.view = view;
    }

    @Provides @Singleton
    FeaturedView provideView(){
        return view;
    }

    @Provides @Singleton
    FeaturedPresenter providePresenter(FeaturedView view,
                                             ChipperService service,
                                             PlaylistManager playlistManager,
                                             VoteManager voteManager,
                                             ChiptuneProvider provider,
                                             @CurrentUser User user){
        return new FeaturedPresenterImpl(view, service, playlistManager, voteManager, provider, user);
    }

}
