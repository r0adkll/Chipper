package com.r0adkll.chipper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.qualifiers.CurrentUser;
import com.r0adkll.chipper.ui.login.LoginActivity;

import javax.inject.Inject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chipper);
        ChipperApp.get(this).inject(this);

        // Apply switch logic here
        if(mCurrentUser == null){

            // Show the Login Activity
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();

        }else{

            Timber.i("Existing user found! User[%s, %s]", mCurrentUser.email, mCurrentUser.id);

            // Show the Starting Activity (All List)
            Intent allChiptunesActivity = new Intent();
//            startActivity(allChiptunesActivity);
            finish();

        }

    }
}
