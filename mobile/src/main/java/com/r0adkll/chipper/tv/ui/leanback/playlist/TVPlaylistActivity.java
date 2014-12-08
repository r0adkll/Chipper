package com.r0adkll.chipper.tv.ui.leanback.playlist;

import android.app.Activity;
import android.os.Bundle;

import com.r0adkll.chipper.tv.ui.model.BaseTVActivity;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.tv.ui.leanback.playlist
 * Created by drew.heavner on 12/8/14.
 */
public class TVPlaylistActivity extends BaseTVActivity implements TVPlaylistView{

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject
    TVPlaylistPresenter presenter;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }





    /***********************************************************************************************
     *
     * View Methods
     *
     */

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void refreshContent() {

    }

    @Override
    public void showSnackBar(String text) {

    }

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new TVPlaylistModule(this)
        };
    }
}
