package com.r0adkll.chipper.tv.ui.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.account.GoogleAccountManager;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.utils.prefs.StringPreference;
import com.r0adkll.chipper.utils.Tools;
import com.r0adkll.deadskunk.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by r0adkll on 12/7/14.
 */
public class TVLoginPresenterImpl implements TVLoginPresenter{

    public static final String ACCOUNT_TYPE = "r0adkll.com";

    private TVLoginView mView;
    private ChipperService mChipperService;
    private SharedPreferences mPrefs;
    private StringPreference mPrefAccountName;

    /**
     * Constructor
     */
    public TVLoginPresenterImpl(TVLoginView view, ChipperService chipperService, SharedPreferences prefs){
        mView = view;
        mChipperService = chipperService;
        mPrefs = prefs;
        mPrefAccountName = new StringPreference(mPrefs, GoogleAccountManager.PREF_ACCOUNT_NAME);
    }

    @Override
    public void createUserAccount(String email, String password) {

        mChipperService.create(email, password, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                handleSuccess(user);
            }

            @Override
            public void failure(RetrofitError error) {
                handleRetrofitError(error);
            }
        });

    }

    @Override
    public void loginToAccount(String email, String password) {

        mChipperService.login(email, password, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                handleSuccess(user);
            }

            @Override
            public void failure(RetrofitError error) {
                handleRetrofitError(error);
            }
        });

    }

    @Override
    public void authorizeUserAccount(final String email, String accessToken) {

        mChipperService.auth(email, accessToken, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                handleSuccess(user);
                mPrefAccountName.set(email);
            }

            @Override
            public void failure(RetrofitError error) {
                handleRetrofitError(error);
            }
        });

    }

    /**
     * Handle a successful login/create request from
     * the server
     *
     * @param user      the user retreived from the server
     */
    private void handleSuccess(final User user){

        // Set this user object as the current user
        user.isCurrentUser = true;

        // Save User Account via activeandroid
        if(user.save() > 0){

            // Associate Pre-Configured Favorites playlist to this user
            Playlist favorites = new Select()
                    .from(Playlist.class)
                    .where("name=?", "Favorites")
                    .limit(1)
                    .executeSingle();

            // If we found the favorites, update it
            if(favorites != null){
                favorites.owner = user;
                favorites.updated_by_user = user;
                favorites.updated = Tools.time();
                favorites.save();
                Timber.i("Successfully linked pre-configured 'Favorites' to the new user");
            }else{
                favorites = new Playlist();
                favorites.name = "Favorites";
                favorites.updated_by_user = user;
                favorites.owner = user;
                favorites.updated = Tools.time();
                favorites.save();
                Timber.i("Successfully created new pre-configured 'Favorites' for the new user");
            }

            // Now create a sync account
            //if (createSyncAccount(mView.getActivity(), user)) {
                // Great Success! Forward on to the next activity
                Timber.i("Great Success! %s has been logged in to chipper with a user id of %s", user.email, user.id);

                // Now register a device
                mChipperService.registerDevice(
                        Tools.generateUniqueDeviceId(mView.getActivity()),
                        Build.MODEL,
                        Build.VERSION.SDK_INT,
                        Utils.isTablet(mView.getActivity()),
                        new Callback<Device>() {
                            @Override
                            public void success(Device device, Response response) {
                                // Store device
                                if (device.save() > 0) {
                                    Timber.i("Device regsitered: %s", device.id);
                                    launchMainActivity();
                                } else {
                                    mView.showErrorMessage("Unable to register device, please try again");
                                }

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                handleRetrofitError(error);
                            }
                        });

//            } else {
//                user.delete();
//                mView.reset();
//                mView.showErrorMessage("Unable to create an Account, please try again");
//            }

        }else{
            mView.showErrorMessage("Unable to save user, please try signing in again.");
        }
    }

    /**
     * Create a new system sync account to use to synchronize the user's data
     * between this device and the server
     *
     * @param activity      the activity this is called from
     * @param user          the user to create an account for
     * @return              true if this was a success, false otherwise
     */
    public boolean createSyncAccount(Activity activity, User user){

        // Create new account
        final Account newAcct = new Account(user.email, ACCOUNT_TYPE);

        // Get account manager instance
        final AccountManager accountManager = AccountManager.get(activity);

        // Find existing accounts
        boolean hasExistingAcct = false;
        Account[] accts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if(accts.length > 0){
            for(Account acct: accts){
                if(acct.name.equals(user.email)){
                    // Found Account!
                    hasExistingAcct = true;
                }
            }
        }

        if(!hasExistingAcct) {
            if (accountManager.addAccountExplicitly(newAcct, null, null)) {
                Timber.i("Account Created: [%s][%s]", newAcct.name, newAcct.type);
                return true;
            } else {
                Timber.e("Unable to create SyncAccount");
                return false;
            }
        }else{
            return true;
        }
    }

    /**
     * Launch into the main portion of the application
     */
    private void launchMainActivity(){
        Timber.i("TV Launch Main Activity");
//        Intent main = new Intent(mView.getActivity(), ChiptunesActivity.class);
//        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mView.getActivity().startActivity(main);
//        mView.close();
    }

    /**
     * Handle the retrofit error from the chipper api
     * @param error
     */
    private void handleRetrofitError(RetrofitError error){
        mView.showErrorMessage(error.getLocalizedMessage());
    }

}
