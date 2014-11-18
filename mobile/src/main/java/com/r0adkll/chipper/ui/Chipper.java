package com.r0adkll.chipper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.core.api.ApiModule;
import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.Device;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.qualifiers.CurrentDevice;
import com.r0adkll.chipper.core.qualifiers.CurrentUser;
import com.r0adkll.chipper.core.utils.Tools;
import com.r0adkll.chipper.ui.all.ChiptunesActivity;
import com.r0adkll.chipper.ui.login.LoginActivity;
import com.r0adkll.deadskunk.utils.Utils;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * This will be a ghost activity that contains NO ui and merely acts as a gateway to other activities
 */
public class Chipper extends Activity {

    /**
     * This should inject the current user if available,
     * otherwise it should be null
     */
    @Inject @CurrentUser
    User mCurrentUser;

    @Inject @CurrentDevice
    Device mCurrentDevice;

    @Inject
    ChipperService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chipper);
        ChipperApp.get(this).inject(this);

        // Apply switch logic here
        if(mCurrentUser == null && mCurrentDevice == null){

            // Show the Login Activity
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();

        }else{

            if(mCurrentDevice != null) {

                Timber.i("Existing user found! User[%s, %s]", mCurrentUser.email, mCurrentUser.id);

                // Show the Starting Activity (All List)
                Intent main = new Intent(this, ChiptunesActivity.class);
                startActivity(main);
                finish();

            }else{

                // Register Device
                mService.registerDevice(mCurrentUser.id,
                        Tools.generateUniqueDeviceId(this),
                        String.format("%s-%s", Build.MANUFACTURER, Build.MODEL),
                        Build.VERSION.SDK_INT,
                        Utils.isTablet(this),
                        new Callback<Device>() {
                            @Override
                            public void success(Device device, Response response) {
                                if(device.save() > 0){

                                    // Show the Starting Activity (All List)
                                    Intent main = new Intent(Chipper.this, ChiptunesActivity.class);
                                    startActivity(main);
                                    finish();
                                }else{
                                    finish();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                finish();
                            }
                        });

            }

        }

    }
}
