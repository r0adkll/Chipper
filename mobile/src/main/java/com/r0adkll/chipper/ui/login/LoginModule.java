package com.r0adkll.chipper.ui.login;

import android.app.Application;

import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.ui.UIModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.login
 * Created by drew.heavner on 11/12/14.
 */
@Module(
    injects = LoginActivity.class,
    addsTo = UIModule.class,
    complete = false
)
public class LoginModule {

    private LoginView view;

    public LoginModule(LoginView view){
        this.view = view;
    }

//    @Provides @Singleton
//    public LoginView provideView(){
//        return view;
//    }

    @Provides @Singleton
    public LoginPresenter providePresenter(ChipperService service, Application app){
        return new LoginPresenterImpl(view, service, app);
    }

}
