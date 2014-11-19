package com.r0adkll.chipper.ui.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ApiModule;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.ChipperError;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.utils.Tools;
import com.r0adkll.chipper.ui.all.ChiptunesActivity;
import com.r0adkll.deadskunk.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.login
 * Created by drew.heavner on 11/12/14.
 */
public class LoginPresenterImpl implements LoginPresenter {

    public static final String ACCOUNT_TYPE = "r0adkll.com";

    private LoginView mView;
    private ChipperService mChipperService;
    private Context mCtx;

    /**
     * Constructor
     */
    public LoginPresenterImpl(LoginView view, ChipperService chipperService, Application app){
        mView = view;
        mChipperService = chipperService;
        mCtx = app;
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
    public void authorizeUserAccount(String email, String accessToken) {

        mChipperService.auth(email, accessToken, new Callback<User>() {
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

    /**
     * Handle a successful login/create request from
     * the server
     *
     * @param user      the user retreived from the server
     */
    private void handleSuccess(User user){

        // Set this user object as the current user
        user.isCurrentUser = true;

        // Save User Account via activeandroid
        if(user.save() > 0){

            // Now create a sync account
            if(createSyncAccount(mView.getActivity(), user)) {

                // Great Success! Forward on to the next activity
                Timber.i("Great Success! %s has been logged in to chipper with a user id of %s", user.email, user.id);

                // Now register a device
                mChipperService.registerDevice(user.id,
                        Tools.generateUniqueDeviceId(mCtx),
                        Build.MODEL,
                        Build.VERSION.SDK_INT,
                        Utils.isTablet(mCtx),
                        new Callback<Device>() {
                            @Override
                            public void success(Device device, Response response) {
                                // Store device
                                if (device.save() > 0) {
                                    Timber.i("Device regsitered: %s", device.id);
                                    launchMainActivity();
                                } else {
                                    mView.showErroMessage("Unable to register device, please try again");
                                }

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                handleRetrofitError(error);
                            }
                        });

            }else{
                user.delete();
                mView.reset();
                mView.showErroMessage("Unable to create an Account, please try again");
            }

        }else{
            mView.showErroMessage("Unable to save user, please try signing in again.");
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
        Account newAcct = new Account(user.email, ACCOUNT_TYPE);

        // Get account manager instance
        AccountManager accountManager = AccountManager.get(activity);

        if(accountManager.addAccountExplicitly(newAcct, null, null)){

            // Added was a success
            Timber.i("Account Created: [%s][%s]", newAcct.name, newAcct.type);
            return true;

        }else{

            Timber.e("Unable to create SyncAccount");
            return false;

        }

    }

    /**
     * Launch into the main portion of the application
     */
    private void launchMainActivity(){
        Intent main = new Intent(mCtx, ChiptunesActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(main);
        mView.close();
    }

    /**
     * Handle the retrofit error from the chipper api
     * @param error
     */
    private void handleRetrofitError(RetrofitError error){
        try {
            ChipperError cer = (ChipperError) error.getBodyAs(ChipperError.class);
            if (cer != null) {
                Timber.e("Retrofit Error[%s] - %s", error.getMessage(), cer.technical);
                mView.showErroMessage(cer.readable);
            } else {
                Timber.e("Retrofit Error: %s", error.getKind().toString());
                mView.showErroMessage(error.getLocalizedMessage());
            }
        }catch (Exception e){
            mView.showErroMessage(error.getMessage());
        }
    }

}
