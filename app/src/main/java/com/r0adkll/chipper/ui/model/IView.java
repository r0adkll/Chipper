package com.r0adkll.chipper.ui.model;

import android.app.Activity;

/**
 * Created by r0adkll on 12/4/14.
 */
public interface IView {

    public Activity getActivity();

    public void refreshContent();

    public void showSnackBar(String text);

}
