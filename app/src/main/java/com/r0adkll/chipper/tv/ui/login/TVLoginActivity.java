package com.r0adkll.chipper.tv.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.nispok.snackbar.Snackbar;
import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.ui.model.BaseActivity;
import com.r0adkll.chipper.tv.ui.model.BaseTVActivity;
import com.r0adkll.chipper.utils.CallbackHandler;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by r0adkll on 12/7/14.
 */
public class TVLoginActivity extends BaseTVActivity implements TVLoginView, View.OnClickListener {


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

    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.sign_in_button)
    SignInButton mSignIn;
    @InjectView(R.id.skip_with_temp_acct)
    TextView mTempAccount;

    @Inject
    TVLoginPresenter presenter;

    private GoogleApiClient mAPIClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    /**********************************************************************************************
     *
     * LifeCycle Methods
     *
     */

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
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .setAccountName("veedubusc@gmail.com")
                .build();

        // Set the click listener for the sign in button
        mSignIn.setOnClickListener(this);
        mTempAccount.setOnClickListener(this);
        mSignIn.setSize(SignInButton.SIZE_WIDE);

        // Update Title
        mTitle.setText(R.string.app_name_tv);

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
                mSignIn.setEnabled(true);
            }

            mIntentInProgress = false;

            if (!mAPIClient.isConnecting()) {
                mAPIClient.connect();
            }
        }else if(requestCode == AUTH_CODE_REQUEST_CODE){
            if(resultCode != RESULT_OK){
                mSignInClicked = false;
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
                }
            });
        }

    }

    /**********************************************************************************************
     *
     * Helper Methods
     *
     */

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sign_in_button) {
            if (!mAPIClient.isConnecting() && !mAPIClient.isConnected()) {
                mSignIn.setEnabled(false);
                mSignInClicked = true;
                resolveSignInError();
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
                    String accessToken = GoogleAuthUtil.getToken(TVLoginActivity.this,
                            Plus.AccountApi.getAccountName(mAPIClient),
                            "oauth2:" + TextUtils.join(" ", SCOPES));

                    return accessToken;
                } catch (IOException transientEx) {
                    // network or server error, the call is expected to succeed if you try again later.
                    // Don't attempt to call again immediately - the request is likely to
                    // fail, you'll hit quotas or back-off.
                    showErrorMessage("Unable to connect to G+, please try again.");
                    Timber.e(transientEx, "Unable to connect to the server");
                } catch (UserRecoverableAuthException e) {
                    // Recover
                    startActivityForResult(e.getIntent(), AUTH_CODE_REQUEST_CODE);
                } catch (GoogleAuthException authEx) {
                    // Failure. The call is not expected to ever succeed so it should not be
                    // retried.
                    showErrorMessage("Unable to log into Google+");
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


    /**********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected Object[] getModules() {
        return new Object[]{
                new TVLoginModule(this)
        };
    }

    /**********************************************************************************************
     *
     * View Methods
     *
     */

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public void refreshContent() {
        // N/A
    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.with(this)
                .text(text)
                .show(this);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reset() {

    }

    @Override
    public void close() {
        finish();
    }
}
