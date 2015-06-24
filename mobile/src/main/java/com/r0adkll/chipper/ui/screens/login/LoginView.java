package com.r0adkll.chipper.ui.screens.login;

import android.app.Activity;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.screens.login
 * Created by drew.heavner on 11/12/14.
 */
public interface LoginView {

    public void showErroMessage(String message);

    public void reset();

    public void close();

    public Activity getActivity();

}
