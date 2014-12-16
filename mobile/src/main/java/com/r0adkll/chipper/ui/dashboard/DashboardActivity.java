package com.r0adkll.chipper.ui.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.data.Historian;
import com.r0adkll.chipper.playback.events.PlaybackStartedEvent;
import com.r0adkll.chipper.ui.adapters.DashboardCardAdapter;
import com.r0adkll.chipper.ui.adapters.OnItemClickListener;
import com.r0adkll.chipper.ui.dashboard.model.DashboardCard;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.model.DividerSpacerItemDecoration;
import com.r0adkll.chipper.ui.player.MusicPlayer;
import com.r0adkll.chipper.ui.player.MusicPlayerCallbacks;
import com.r0adkll.chipper.utils.UIUtils;
import com.r0adkll.deadskunk.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 12/14/14.
 */
public class DashboardActivity extends BaseDrawerActivity implements DashboardView, OnItemClickListener<Historian.Chronicle>,MusicPlayerCallbacks {

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

    @InjectView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @InjectView(R.id.fab_shuffle_play)
    FrameLayout mFabShufflePlay;

    @Inject Bus mBus;
    @Inject DashboardPresenter presenter;
    @Inject DashboardCardAdapter adapter;

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
        overridePendingTransition(0, 0);
        getSupportActionBar().setTitle(R.string.navdrawer_item_dashboard);

        // Setup the music player callbacks
        getPlayer().setCallbacks(this);

        UIUtils.setupFAB(this, mFabShufflePlay);
        mFabShufflePlay.setOnClickListener(mFabClickListener);

        int orientation = LinearLayoutManager.VERTICAL;
        float spacing = 8f;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            orientation = LinearLayoutManager.HORIZONTAL;
            spacing = 16f;
        }

        // Setup recycler View
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerSpacerItemDecoration(Utils.dpToPx(this, spacing), true));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, orientation, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Load dasboard cards
        presenter.loadDashboardCards();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
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

    @Subscribe
    public void answerPlaybackStartedEvent(PlaybackStartedEvent event){
        adapter.notifyDataSetChanged();
    }

    /***********************************************************************************************
     *
     * View Methods
     *
     */

    @Override
    public void onItemClick(View v, Historian.Chronicle item, int position) {
        presenter.onChronicleSelected(item);
    }

    @Override
    public void setDashboardCards(List<DashboardCard> cards) {
        adapter.clear();
        adapter.addAll(cards);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void refreshContent() {

    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.with(this)
                .text(text)
                .show(this);
    }

    @Override
    public void onStarted() {
        if(getSlidingLayout().isPanelHidden()) {
            // Start playing a random tune on shuffle
            ObjectAnimator anim = ObjectAnimator.ofFloat(mFabShufflePlay, "alpha", 1, 0)
                    .setDuration(300);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFabShufflePlay.setVisibility(View.GONE);
                }
            });

            anim.start();
            getSlidingLayout().showPanel();
        }
    }

    @Override
    public void onStopped() {
        if(!getSlidingLayout().isPanelHidden()) {
            // Animate the FAB back onto screen
            // Start playing a random tune on shuffle
            ObjectAnimator anim = ObjectAnimator.ofFloat(mFabShufflePlay, "alpha", 0, 1)
                    .setDuration(300);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mFabShufflePlay.setVisibility(View.VISIBLE);
                }
            });

            anim.start();
            getSlidingLayout().hidePanel();
        }
    }

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
