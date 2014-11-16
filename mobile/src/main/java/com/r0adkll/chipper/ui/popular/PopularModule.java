package com.r0adkll.chipper.ui.popular;

import com.r0adkll.chipper.adapters.PopularChiptuneAdapter;
import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.data.ChiptuneProvider;
import com.r0adkll.chipper.core.qualifiers.CurrentUser;
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
                                      @CurrentUser User user){
        return new PopularPresenterImpl(view, provider, service, user);
    }

    @Provides
    PopularChiptuneAdapter provideAdapter(){
        return new PopularChiptuneAdapter();
    }

}
