package com.r0adkll.chipper.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.player.MusicPlayer;
import com.r0adkll.chipper.utils.UIUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 12/14/14.
 */
public class DashboardActivity extends BaseDrawerActivity implements DashboardView{

    /***********************************************************************************************
     *
     * Constants
     *
     */

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @InjectView(R.id.recents_list)
    RecyclerView mRecentsList;

    @InjectView(R.id.fab_shuffle_play)
    FrameLayout mFabShufflePlay;

    @Inject DashboardPresenter presenter;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.inject(this);
        UIUtils.setupFAB(this, mFabShufflePlay);
        mFabShufflePlay.setOnClickListener(mFabClickListener);

    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    private View.OnClickListener mFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = MusicPlayer.createShufflePlayback(DashboardActivity.this);
            MusicPlayer.startPlayback(DashboardActivity.this, intent);
        }
    };

    /***********************************************************************************************
     *
     * View Methods
     *
     */




    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_DASHBOARD;
    }

    @Override
    protected void onNavDrawerSlide(float offset) {}

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new DashboardModule(this)
        };
    }
}
