package com.r0adkll.chipper.core.push;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.push
 * Created by drew.heavner on 11/18/14.
 */
public class PushUtils {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * The GCM sender id to correllate to the push system
     */
    public static final String SENDER_ID = "228196429554";

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity, int resolutionRequestCode) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        resolutionRequestCode).show();
            } else {
                Timber.i("This device is not supported.");
            }
            return false;
        }
        return true;
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
