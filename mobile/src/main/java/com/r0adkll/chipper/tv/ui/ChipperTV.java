package com.r0adkll.chipper.tv.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.push.PushManager;
import com.r0adkll.chipper.qualifiers.CurrentDevice;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.tv.ui.leanback.ChipperLeanback;
import com.r0adkll.chipper.tv.ui.login.TVLoginActivity;
import com.r0adkll.chipper.utils.Tools;
import com.r0adkll.deadskunk.utils.Utils;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by r0adkll on 12/7/14.
 */
public class ChipperTV extends Activity{

    @Inject @CurrentUser
    User mCurrentUser;

    @Inject @CurrentDevice
    Device mCurrentDevice;

    @Inject
    ChipperService mService;

    @Inject
    PushManager mPushManager;

    @Inject
    VoteManager mVoteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChipperApp.get(this).inject(this);

        // Apply switch logic here
        if(mCurrentUser == null && mCurrentDevice == null){

            // Show the Login Activity
            Intent loginActivity = new Intent(this, TVLoginActivity.class);
            startActivity(loginActivity);
            finish();

        }else{

            // Log
            Timber.i("Existing user found! User[%s, %s]", mCurrentUser.email, mCurrentUser.id);

            if(mCurrentDevice != null) {

                Timber.i("Existing device found! Device[%s, %s]", mCurrentDevice.device_id, mCurrentDevice.id);

                // Recheck push registration
                mPushManager.checkRegistration(this);

                // Sync the user's votes
                mVoteManager.syncUserVotes();

                // Show the Starting Activity (All List)
                Intent main = new Intent(this, ChipperLeanback.class);
                startActivity(main);
                finish();

            }else{

                Timber.i("User found, but device was not. Registering new device");

                // Register Device
                mService.registerDevice(
                        Tools.generateUniqueDeviceId(this),
                        String.format("%s-%s", Build.MANUFACTURER, Build.MODEL),
                        Build.VERSION.SDK_INT,
                        Utils.isTablet(this),
                        new Callback<Device>() {
                            @Override
                            public void success(Device device, Response response) {
                                if(device.save() > 0){

                                    Timber.i("New Device registered [%s]", device.id);

                                    // Recheck push registration
                                    mPushManager.checkRegistration(ChipperTV.this);

                                    // Sync the user's votes
                                    mVoteManager.syncUserVotes();

                                    // Show the Starting Activity (All List)
                                    Intent main = new Intent(ChipperTV.this, ChipperLeanback.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPushManager.handleActivityResult(this, requestCode, resultCode, data);
    }
}
