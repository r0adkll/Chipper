package com.r0adkll.chipper.ui.popular;

import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.ui.adapters.PopularChiptuneAdapter;
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
 * Created by r0adkll on 11/15/14.
 */
@Module(
    injects = PopularActivity.class,
    addsTo = UIModule.class,
    complete = false
)
public class PopularModule {
    private PopularView view;

    public PopularModule(PopularView view){
        this.view = view;
    }

    @Provides @Singleton
    PopularView provideView(){
        return view;
    }

    @Provides @Singleton
    PopularPresenter providePresenter(PopularView view,
                                      ChiptuneProvider provider,
                                      ChipperService service,
                                      VoteManager voteManager,
                                      PlaylistManager playlistManager,
                                      @CurrentUser User user){

        return new PopularPresenterImpl(view, provider, service, playlistManager, voteManager, user);
    }

    @Provides
    PopularChiptuneAdapter provideAdapter(PlaylistManager playlistManager,
                                          VoteManager voteManager,
                                          CashMachine cashMachine){

        return new PopularChiptuneAdapter(playlistManager, voteManager, cashMachine);
    }

}
