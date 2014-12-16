package com.r0adkll.chipper.ui.dashboard;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.ui.UIModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 12/14/14.
 */
@Module(
    injects = DashboardActivity.class,
    addsTo = UIModule.class,
    complete = false
)
public class DashboardModule {
    private DashboardView view;

    public DashboardModule(DashboardView view){
        this.view = view;
    }

    @Provides @Singleton
    DashboardView provideView(){
        return view;
    }

    @Provides @Singleton
    DashboardPresenter providePresenter(DashboardView view,
                                        ChipperService service){
        return new DashboardPresenterImpl(view, service);
    }

}
