package com.r0adkll.chipper.ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.activeandroid.content.ContentProvider;
import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.account.GoogleAccountManager;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.data.model.PlaylistObserver;
import com.r0adkll.chipper.push.PushManager;
import com.r0adkll.chipper.push.PushUtils;
import com.r0adkll.chipper.qualifiers.CurrentDevice;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.ui.dashboard.DashboardActivity;
import com.r0adkll.chipper.utils.Tools;
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

    @Inject
    VoteManager mVoteManager;

    @Inject
    ChiptuneProvider mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChipperApp.get(this).inject(this);

        // Apply switch logic here
        if(mCurrentUser == null && mCurrentDevice == null){

            // Show the Login Activity
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();

        }else{

            // Log
            Timber.i("Existing user found! User[%s, %s]", mCurrentUser.email, mCurrentUser.id);

            if(mCurrentDevice != null) {

                Timber.i("Existing device found! Device[%s, %s]", mCurrentDevice.device_id, mCurrentDevice.id);

                // Load all chiptunes into memory
                mProvider.loadChiptunes(null);

                // Set the content observer
                setupContentObserver();

                // Sync the user's votes
                mVoteManager.syncUserVotes(mCurrentUser.id);

                // Show the Starting Activity (All List)
                Intent main = new Intent(this, DashboardActivity.class);
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

                                    // Load all chiptunes into memory
                                    mProvider.loadChiptunes(null);

                                    // Set the content observer
                                    setupContentObserver();

                                    // Force an initial Sync
                                    initialSync();

                                    // Sync the user's votes
                                    mVoteManager.syncUserVotes(mCurrentUser.id);

                                    // Show the Starting Activity (All List)
                                    Intent main = new Intent(Chipper.this, DashboardActivity.class);
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

    private void initialSync(){
        // Force an initial sync
        ContentResolver.requestSync(new Account(mCurrentUser.email, GoogleAccountManager.ACCOUNT_TYPE),
                GoogleAccountManager.AUTHORITY, new Bundle());
    }

    /**
     * Setup the content observer that triggers account syncs with the server
     */
    private void setupContentObserver(){
        ContentResolver resolver = getContentResolver();
        Uri uri = ContentProvider.createUri(Playlist.class, null);

        PlaylistObserver observer = new PlaylistObserver(this, new Handler());
        resolver.registerContentObserver(uri, true, observer);
    }
}
