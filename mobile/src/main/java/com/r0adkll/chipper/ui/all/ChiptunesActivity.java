package com.r0adkll.chipper.ui.all;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.playback.MusicService;
import com.r0adkll.chipper.ui.adapters.AllChiptuneAdapter;
import com.r0adkll.chipper.ui.adapters.OnItemClickListener;
import com.r0adkll.chipper.ui.adapters.RecyclerArrayAdapter;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.player.MusicPlayer;
import com.r0adkll.chipper.ui.player.MusicPlayerCallbacks;
import com.r0adkll.chipper.ui.widget.StickyRecyclerHeadersElevationDecoration;
import com.r0adkll.chipper.utils.UIUtils;
import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.styles.EditTextStyle;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 11/12/14.
 */
public class ChiptunesActivity extends BaseDrawerActivity
        implements ChiptunesView, OnItemClickListener<Chiptune>,RecyclerArrayAdapter.OnItemOptionSelectedListener<Chiptune>,MusicPlayerCallbacks {

    /***********************************************************************************************
     *
     *  Variables
     *
     */

    @InjectView(R.id.chiptune_recycler)
    SwipeListView mChiptuneRecycler;

    @InjectView(R.id.fab_shuffle_play)
    FrameLayout mFABShufflePlay;

    @Inject
    ChiptunesPresenter presenter;

    @Inject
    AllChiptuneAdapter mAdapter;


    /***********************************************************************************************
     *
     *  Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chiptunes);
        ButterKnife.inject(this);
        overridePendingTransition(0, 0);
        getSupportActionBar().setTitle(R.string.navdrawer_item_chiptunes);

        // Setuyp the FAB
        UIUtils.setupFAB(this, mFABShufflePlay);
        mFABShufflePlay.setOnClickListener(mFABClickListener);

        // Setup the music player callbacks
        getPlayer().setCallbacks(this);

        // Setup the adapter with the recycler view
        mChiptuneRecycler.setAdapter(mAdapter);
        mChiptuneRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        StickyRecyclerHeadersElevationDecoration headersDecor = new StickyRecyclerHeadersElevationDecoration(mAdapter);
        mChiptuneRecycler.addItemDecoration(headersDecor);
        mAdapter.setOnItemOptionSelectedListener(this);

        mChiptuneRecycler.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public void onClickFrontView(int position) {
                presenter.onChiptuneSelected(mAdapter.getItem(position));
            }
        });

        //  Load all chiptunes
        presenter.loadAllChiptunes();
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    @Override
    public void onItemClick(View view, Chiptune item,  int position) {
        presenter.onChiptuneSelected(item);
    }

    @Override
    public void onSelected(View view, Chiptune item) {
        switch (view.getId()){
            case R.id.opt_favorite:
                presenter.favoriteChiptunes(item);
                break;
            case R.id.opt_upvote:
                presenter.upvoteChiptune(item);
                break;
            case R.id.opt_downvote:
                presenter.downvoteChiptune(item);
                break;
            case R.id.opt_add:
                presenter.addChiptunesToPlaylist(item);
                break;
            case R.id.opt_offline:
                presenter.offlineChiptunes(item);
                break;
        }
        mChiptuneRecycler.closeOpenedItems();
    }

    /**
     * The floating action button click listener
     */
    private View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Prepare intent to start playback
            Intent playback = MusicPlayer.createShufflePlayback(getActivity());
            startService(playback);

        }
    };

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

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

    @Override
    public Activity getActivity(){
        return this;
    }

    @Override
    public void setChiptunes(List<Chiptune> chiptunes) {
        mAdapter.clear();
        mAdapter.addAll(chiptunes);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showErrorMessage(String msg) {
        PostOffice.newMail(this)
                .setMessage(msg)
                .show(getSupportFragmentManager());
    }

    @Override
    public void onStarted() {
        if(getSlidingLayout().isPanelHidden()) {
            // Start playing a random tune on shuffle
            ObjectAnimator anim = ObjectAnimator.ofFloat(mFABShufflePlay, "alpha", 1, 0)
                    .setDuration(300);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFABShufflePlay.setVisibility(View.GONE);
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
            ObjectAnimator anim = ObjectAnimator.ofFloat(mFABShufflePlay, "alpha", 0, 1)
                    .setDuration(300);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mFABShufflePlay.setVisibility(View.VISIBLE);
                }
            });

            anim.start();
            getSlidingLayout().hidePanel();
        }
    }
}
