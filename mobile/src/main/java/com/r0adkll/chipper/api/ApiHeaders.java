package com.r0adkll.chipper.api;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.qualifiers.DeviceId;
import com.r0adkll.chipper.utils.Tools;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.api
 * Created by drew.heavner on 11/17/14.
 */
@Singleton
public final class ApiHeaders implements RequestInterceptor{
    private static final String AUTHORIZATION_HEADER = "auth";

    @Inject
    Gson gson;

    @Inject @DeviceId
    String deviceId;

    @Inject
    public ApiHeaders(){}

    @Override
    public void intercept(RequestFacade request) {

        // Get the current logged in user
        User currentUser = new Select()
                .from(User.class)
                .where("is_current_user=?", true)
                .limit(1)
                .executeSingle();

        // Get the current device
        Device currentDevice = new Select()
                .from(Device.class)
                .where("device_id=?", deviceId)
                .limit(1)
                .executeSingle();

        if(currentUser != null && currentDevice == null){
            request.addHeader(AUTHORIZATION_HEADER, ApiModule.generateUserAuthParam(gson, currentUser));
        }else if(currentUser != null && currentDevice != null){
            request.addHeader(AUTHORIZATION_HEADER, ApiModule.generateDeviceAuthParam(gson, currentDevice));
        }
    }
}
