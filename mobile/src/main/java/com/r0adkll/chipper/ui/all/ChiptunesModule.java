package com.r0adkll.chipper.ui.all;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.qualifiers.CurrentUser;
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
    ChiptunesPresenter providePresenter(ChiptunesView chiptunesView,
                                        ChiptuneProvider provider,
                                        ChipperService service,
                                        @CurrentUser User user){
        return new ChiptunesPresenterImpl(chiptunesView, provider, service, user);
    }

}
