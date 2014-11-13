package com.r0adkll.chipper.ui.all;

import android.os.Bundle;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;

import javax.inject.Inject;

/**
 * Created by r0adkll on 11/12/14.
 */
public class ChiptunesActivity extends BaseDrawerActivity implements ChiptunesView{

    /***********************************************************************************************
     *
     *  Variables
     *
     */

    @Inject
    ChiptunesPresenter presenter;

    /***********************************************************************************************
     *
     *  Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chiptunes);

    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_CHIPTUNES;
    }

    @Override
    protected void onNavDrawerSlide(float offset) {

    }

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new ChiptunesModule(this)
        };
    }

    /***********************************************************************************************
     *
     *  View Methods
     *
     */



}
