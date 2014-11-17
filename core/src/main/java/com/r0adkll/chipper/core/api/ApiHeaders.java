package com.r0adkll.chipper.core.api;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.qualifiers.CurrentUser;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.api
 * Created by drew.heavner on 11/17/14.
 */
@Singleton
public final class ApiHeaders implements RequestInterceptor{
    private static final String AUTHORIZATION_HEADER = "auth";

    @Inject
    public ApiHeaders(){}

    @Override
    public void intercept(RequestFacade request) {
        User currentUser = new Select()
                .from(User.class)
                .where("is_current_user=?", true)
                .limit(1)
                .executeSingle();

        if(currentUser != null){
            request.addHeader(AUTHORIZATION_HEADER, ApiModule.generateAuthParam(currentUser));
        }
    }
}
