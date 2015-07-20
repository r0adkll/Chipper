package com.r0adkll.chipper.api;

import com.google.gson.Gson;
import com.r0adkll.chipper.data.model.Device;
import com.r0adkll.chipper.data.model.User;
import com.r0adkll.chipper.utils.qualifiers.DeviceId;

import javax.inject.Inject;
import javax.inject.Singleton;

import ollie.query.Select;
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
        User currentUser = Select.from(User.class)
                .where("is_current_user=?", true)
                .limit("1")
                .fetchSingle();

        // Get the current device
        Device currentDevice = Select.from(Device.class)
                .where("device_id=?", deviceId)
                .limit("1")
                .fetchSingle();

        if(currentUser != null && currentDevice == null){
            request.addHeader(AUTHORIZATION_HEADER, ApiModule.generateUserAuthParam(gson, currentUser));
        }else if(currentUser != null && currentDevice != null){
            request.addHeader(AUTHORIZATION_HEADER, ApiModule.generateDeviceAuthParam(gson, currentUser, currentDevice));
        }
    }
}
