package com.r0adkll.chipper.ui.all;

import android.app.Activity;

import com.r0adkll.chipper.api.model.Chiptune;

import java.util.List;

/**
 * Created by r0adkll on 11/13/14.
 */
public interface ChiptunesView {

    public void setChiptunes(List<Chiptune> chiptunes);

    public void showProgress();

    public void hideProgress();

    public void showErrorMessage(String msg);

    public Activity getActivity();

}
