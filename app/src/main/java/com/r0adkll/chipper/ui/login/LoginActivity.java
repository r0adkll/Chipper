package com.r0adkll.chipper.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.internal.widget.ViewUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.qualifiers.GenericPrefs;
import com.r0adkll.chipper.ui.widget.ScrimInsetsRelativeLayout;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.chipper.ui.model.BaseActivity;
import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.PostOffice;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui
 * Created by drew.heavner on 11/12/14.
 */
public class LoginActivity extends BaseActivity implements LoginView, View.OnClickListener {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    private static final int RC_SIGN_IN = 1001;
    private static final int AUTH_CODE_REQUEST_CODE = 1002;

    private static final Object[] SCOPES = new String[]{
            "https://www.googleapis.com/auth/plus.login",
            "https://www.googleapis.com/auth/plus.me",
            "https://www.googleapis.com/auth/userinfo.email"
    };


    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject LoginPresenter presenter;
    @InjectView(R.id.sign_in_button)        SignInButton mSignIn;
    @InjectView(R.id.skip_with_temp_acct)   TextView mTempAccount;
    @InjectView(R.id.container)             ScrimInsetsRelativeLayout mContainer;
    @InjectView(R.id.loading)               ProgressBar mProgress;

    private GoogleApiClient mAPIClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    /**
     * Called to create this activity
     * @param savedInstanceState
     */
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        // Create the Plus Client
        mAPIClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        // Set the click listener for the sign in button
        mSignIn.setOnClickListener(this);
        mTempAccount.setOnClickListener(this);
        mSignIn.setSize(SignInButton.SIZE_WIDE);

        mContainer.setOnInsetsCallback(new ScrimInsetsRelativeLayout.OnInsetsCallback() {
            @Override
            public void onInsetsChanged(Rect insets) {
//                mContainer.setPadding(insets.left, insets.top, insets.right, insets.bottom);
                mTempAccount.setPadding(0, 0, 0, insets.bottom);
            }
        });

        // Set Translucent decor
        if(Utils.isKitKat()){

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAPIClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAPIClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mAPIClient.isConnecting()) {
                mAPIClient.connect();
            }
        }else if(requestCode == AUTH_CODE_REQUEST_CODE){
            if(resultCode != RESULT_OK){
                mSignInClicked = false;
                mSignIn.setEnabled(true);
                mProgress.setVisibility(View.GONE);
                mSignIn.setVisibility(View.VISIBLE);
            }

            // Try to get the access token again
            getAuthToken(new CallbackHandler<String>() {
                @Override
                public void onHandle(String authToken) {
                    String accountName = Plus.AccountApi.getAccountName(mAPIClient);

                    // Now attempt to log into server
                    presenter.authorizeUserAccount(accountName, authToken);
                }

                @Override
                public void onFailure(String msg) {
                    // Do Nothing
                    mSignInClicked = false;
                    mSignIn.setEnabled(true);
                    mProgress.setVisibility(View.GONE);
                    mSignIn.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    @Override
    public void showErroMessage(String message) {
        PostOffice.newMail(this)
                .setMessage(message)
                .show(getSupportFragmentManager());
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void reset() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new LoginModule(this)
        };
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sign_in_button) {
            if (!mAPIClient.isConnecting() && !mAPIClient.isConnected()) {
                mSignIn.setEnabled(false);
                mSignInClicked = true;
                resolveSignInError();
                mProgress.setVisibility(View.VISIBLE);
                mSignIn.setVisibility(View.GONE);
            } else if (mAPIClient.isConnected()) {
                mSignIn.setEnabled(false);

                // Attempt to get token and register user
                getAuthToken(new CallbackHandler<String>() {
                    @Override
                    public void onHandle(String authToken) {
                        String email = Plus.AccountApi.getAccountName(mAPIClient);
                        presenter.authorizeUserAccount(email, authToken);
                    }

                    @Override
                    public void onFailure(String msg) {
                        // Do Nothing
                        mSignIn.setEnabled(true);
                        mSignInClicked = false;
                        mProgress.setVisibility(View.GONE);
                        mSignIn.setVisibility(View.VISIBLE);
                    }
                });
            }
        }else if(v.getId() == R.id.skip_with_temp_acct){

            // TODO: Show the Login/Create account UI for the user to create/login(into) an account with



        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mAPIClient.connect();
            }
        }
    }

    /**
     * Attempt to get an access token from the client to send to the server for
     * server-side authentication of the account.
     *
     * @return  the access token, or null;
     */
    private void getAuthToken(final CallbackHandler<String> cb){

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {

                try {
                    String accessToken = GoogleAuthUtil.getToken(LoginActivity.this,
                            Plus.AccountApi.getAccountName(mAPIClient),
                            "oauth2:" + TextUtils.join(" ", SCOPES));

                    return accessToken;
                } catch (IOException transientEx) {
                    // network or server error, the call is expected to succeed if you try again later.
                    // Don't attempt to call again immediately - the request is likely to
                    // fail, you'll hit quotas or back-off.
                    showErroMessage("Unable to connect to G+, please try again.");
                    Timber.e(transientEx, "Unable to connect to the server");
                } catch (UserRecoverableAuthException e) {
                    // Recover
                    startActivityForResult(e.getIntent(), AUTH_CODE_REQUEST_CODE);
                } catch (GoogleAuthException authEx) {
                    // Failure. The call is not expected to ever succeed so it should not be
                    // retried.
                    showErroMessage("Unable to log into Google+");
                    Timber.e(authEx, "Unable to authorize Google+ token");

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String token) {
                if(token != null){
                    cb.onHandle(token);
                }else{
                    cb.onFailure("Unable to get token");
                }
            }
        }.execute();
    }



    /**
     * The connection callbacks
     */
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            mSignInClicked = false;
            Timber.i("User is connected to PlayServices!");
            mSignIn.setEnabled(false);

            // Attempt to get token and register user
            getAuthToken(new CallbackHandler<String>() {
                @Override
                public void onHandle(String authToken) {
                    String email = Plus.AccountApi.getAccountName(mAPIClient);
                    presenter.authorizeUserAccount(email, authToken);
                }

                @Override
                public void onFailure(String msg) {
                    // Do Nothing
                    mSignIn.setEnabled(true);
                }
            });

        }

        @Override
        public void onConnectionSuspended(int i) {
            mAPIClient.connect();
        }
    };

    /**
     * The connection failed listener
     */
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            if(!mIntentInProgress){
                mConnectionResult = result;

                if(mSignInClicked){
                    resolveSignInError();
                }

            }
        }
    };
}
