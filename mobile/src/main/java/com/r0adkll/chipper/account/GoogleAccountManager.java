package com.r0adkll.chipper.account;

import android.accounts.Account;
import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.r0adkll.chipper.utils.prefs.StringPreference;
import com.r0adkll.chipper.qualifiers.GenericPrefs;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * This manager will be used to manage all interactions with the GooglePlus API so that each time the
 * user opens the application it grabs the relevant account information from Google's servers
 * (prof ID, Avatar, Cover Photo, etc) and uses it to build the UI.
 *
 * It also listens to certain account callbacks for logging out and de-authorizing the apps
 *
 * Project: Chipper
 * Package: com.r0adkll.chipper.account
 * Created by drew.heavner on 11/28/14.
 */
public class GoogleAccountManager {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final String PREF_ACCOUNT_NAME = "pref_google_account_name";
    public static final String PREF_PROFILE_ID = "pref_google_profile_id";
    public static final String PREF_DISPLAY_NAME = "pref_google_display_name";
    public static final String PREF_IMAGE_URL = "pref_google_image_url";
    public static final String PREF_COVER_URL = "pref_google_cover_url";
    public static final String AUTHORITY = "com.r0adkll.chipper.provider";
    public static final String ACCOUNT_TYPE = "r0adkll.com";

    /***********************************************************************************************
     *
     * Variables
     *
     */

    SharedPreferences mPrefs;

    private GoogleApiClient mClient;
    private OnAccountLoadedListener mLoadListener;

    private boolean mStarted = false;
    private boolean mResolving = false;

    private StringPreference mPrefAccountName;
    private StringPreference mPrefProfileId;
    private StringPreference mPrefDisplayName;
    private StringPreference mPrefImageUrl;
    private StringPreference mPrefCoverUrl;

    /**
     * Injectable Constructor
     */
    @Inject
    public GoogleAccountManager(Application ctx, @GenericPrefs SharedPreferences prefs){
        mPrefs = prefs;

        // Initialize the Play Client
        mClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        // Initialize the Preferences
        mPrefAccountName = new StringPreference(mPrefs, PREF_ACCOUNT_NAME);
        mPrefProfileId = new StringPreference(mPrefs, PREF_PROFILE_ID);
        mPrefDisplayName = new StringPreference(mPrefs, PREF_DISPLAY_NAME);
        mPrefImageUrl = new StringPreference(mPrefs, PREF_IMAGE_URL);
        mPrefCoverUrl = new StringPreference(mPrefs, PREF_COVER_URL);
    }

    /**
     * Set the account load listener
     *
     * @param listener      the listener that will be called when the user's account information has
     *                      been loaded from google
     */
    public void setOnAccountLoadedListener(OnAccountLoadedListener listener){
        mLoadListener = listener;
    }

    /***********************************************************************************************
     *
     * Lifecycle methods
     *
     */

    /**
     * Called when the host activity is started. This will start the process to connect to the
     * signed in account
     */
    public void onStart(){
        mClient.connect();
    }

    public void onStop(){
        if(mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    public String getAccountName(){
        return mPrefAccountName.get();
    }

    public String getProfileId(){
        return mPrefProfileId.get();
    }

    public String getDisplayName(){
        return mPrefDisplayName.get();
    }

    public String getImageUrl(){
        return mPrefImageUrl.get();
    }

    public String getCoverUrl(){
        return mPrefCoverUrl.get();
    }

    public Account getActiveAccount(){
        String name = getAccountName();
        if(name != null){
            return new Account(name, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        }else{
            return null;
        }
    }

    /***********************************************************************************************
     *
     * Callback Methods
     *
     */

    /**
     * The callback for loading the currently logged in person
     *
     */
    private ResultCallback<People.LoadPeopleResult> mLoadPeopleCallback = new ResultCallback<People.LoadPeopleResult>() {
        @Override
        public void onResult(People.LoadPeopleResult loadPeopleResult) {
            if(loadPeopleResult.getStatus().isSuccess()){
                PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                if(personBuffer != null && personBuffer.getCount() > 0){
                    Person currentUser = personBuffer.get(0);
                    personBuffer.close();

                    // Save the profile ID, image url, display name
                    String profileId = currentUser.getId();
                    String displayName = currentUser.getDisplayName();
                    String imageUrl = currentUser.getImage().getUrl();
                    if (imageUrl != null) {
                        imageUrl = Uri.parse(imageUrl)
                                .buildUpon().appendQueryParameter("sz", "256").build().toString();
                    }

                    String coverPhotoUrl = "";
                    Person.Cover cover = currentUser.getCover();
                    if(cover != null){
                        Person.Cover.CoverPhoto coverPhoto = cover.getCoverPhoto();
                        if(coverPhoto != null){
                            coverPhotoUrl = coverPhoto.getUrl();
                        }
                    }

                    // Save all the info into their respective preefences
                    mPrefProfileId.set(profileId);
                    mPrefDisplayName.set(displayName);
                    mPrefImageUrl.set(imageUrl);
                    mPrefCoverUrl.set(coverPhotoUrl);

                    // Call registered callbacks to let the UI know that the profile information has
                    // been loaded
                    if(mLoadListener != null) mLoadListener.onLoaded();
                }
            }
        }
    };

    /**
     * The connection callbacks
     */
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Timber.i("User is connected to PlayServices!");

            // Check for plus info
            if(getProfileId() == null){
                Plus.PeopleApi.load(mClient, "me").setResultCallback(mLoadPeopleCallback);
            }else{
                // No need to load plus info
            }


        }

        @Override
        public void onConnectionSuspended(int i) {
            mClient.connect();
        }
    };

    /**
     * The connection failed listener
     */
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {

        }
    };


    public static interface OnAccountLoadedListener{
        public void onLoaded();
    }

}
