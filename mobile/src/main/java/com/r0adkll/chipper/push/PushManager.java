package com.r0adkll.chipper.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.activeandroid.query.Select;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.utils.prefs.IntPreference;
import com.r0adkll.chipper.utils.prefs.StringPreference;
import com.r0adkll.chipper.qualifiers.AppVersion;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.qualifiers.DeviceId;
import com.r0adkll.chipper.qualifiers.PushToken;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * This is the push management class
 *
 * Project: Chipper
 * Package: com.r0adkll.chipper.push
 * Created by drew.heavner on 11/18/14.
 */
@Singleton
public class PushManager {

    @Inject @CurrentUser
    User mCurrentUser;

    @Inject
    ChipperService mService;

    @Inject
    GoogleCloudMessaging mGcm;

    @Inject
    @DeviceId
    String mDeviceId;

    @Inject
    @AppVersion
    IntPreference mAppVersionPreference;

    @Inject
    @PushToken
    StringPreference mPushTokenPreference;

    @Inject
    public PushManager(){}

    /**
     * Check the GCM registration updating if needed
     *
     * @param activity      the activity this is to be called from
     */
    public void checkRegistration(Activity activity){

        if(PushUtils.checkPlayServices(activity, PushUtils.PLAY_SERVICES_RESOLUTION_REQUEST)){

            String token = getRegistrationId(activity);
            if(token.isEmpty()){
                registerInBackground(activity);
            }

        }else{
            Timber.w("No valid Google Play Services APK found");
        }

    }

    /**
     * Handle an activity result that might occur from checking google play services
     *
     * @param requestCode       the request code
     * @param resultCode        the result code
     * @param data              the intent data
     * @return                  true if this function handled the result, false otherwise
     */
    public boolean handleActivityResult(Activity activity, int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case PushUtils.PLAY_SERVICES_RESOLUTION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    checkRegistration(activity);
                    return true;
                }
                break;
        }

        return false;
    }

    /**
     * Get the devices registration id if it exists
     *
     * @return      the device's registration id, or an empty string
     */
    public String getRegistrationId(Context ctx){
        String registrationId = mPushTokenPreference.get();

        // Check version numbers
        int registeredVersion = mAppVersionPreference.get();
        int currentVersion = PushUtils.getAppVersion(ctx);

        // If the application has been updated, we must aquire a new registration id
        // because the old one isn't guaranteed to work.
        if(registeredVersion != currentVersion){
            Timber.i("GCM: App Version Changed.");
            return "";
        }

        return registrationId;
    }

    /**
     * Register this device in the background for a push token
     *
     * @param context       the context to register with
     */
    public void registerInBackground(final Context context){

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(context);
                    }

                    // Register against GCM
                    String regId = mGcm.register(PushUtils.SENDER_ID);

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.
                    return regId;
                } catch (IOException ex) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return null;
            }

            @Override
            protected void onPostExecute(String regId) {
                if(regId != null) {

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendTokenToServer(regId);

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);

                }
            }
        }.execute();

    }

    /**
     * Store the registration id to the disk
     *
     * @param ctx       the context reference
     * @param regId     the push token to store
     */
    private void storeRegistrationId(Context ctx, String regId){

        // Store the app version for this id
        int appVersion = PushUtils.getAppVersion(ctx);
        mAppVersionPreference.set(appVersion);

        // Store the new push token
        mPushTokenPreference.set(regId);

    }

    /**
     * Send the push token to the server
     *
     * @param token     the push token to send
     */
    private void sendTokenToServer(String token){

        // Get the current device
        final Device device = new Select()
                .from(Device.class)
                .where("device_id=?", mDeviceId)
                .limit(1)
                .executeSingle();

        if(device != null && mCurrentUser != null) {

            // Register this token on the server
            mService.registerPushToken(mCurrentUser.id, device.id, token, new Callback<Device>() {
                @Override
                public void success(Device updatedDevice, Response response) {
                    // Update the current device on the system
                    device.update(updatedDevice);

                    Timber.i("Push Token [%s] registered to device [%s]", updatedDevice.push_token, updatedDevice.id);
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.e(error.getCause(), "Unable to update the server with this registration id");
                }
            });

        }else{
            Timber.e("Unable register push token to server, Couldn't find setup user or device");
        }
    }


}
