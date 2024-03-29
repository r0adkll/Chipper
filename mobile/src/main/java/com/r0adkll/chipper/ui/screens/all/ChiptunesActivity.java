package com.r0adkll.chipper.ui.screens.all;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.EventListener;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.data.events.OfflineModeChangeEvent;
import com.r0adkll.chipper.data.events.OfflineRequestCompletedEvent;
import com.r0adkll.chipper.ui.adapters.AllChiptuneAdapter;
import com.r0adkll.chipper.ui.adapters.OnItemClickListener;
import com.r0adkll.chipper.ui.adapters.RecyclerArrayAdapter;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.screens.player.MusicPlayer;
import com.r0adkll.chipper.ui.screens.player.MusicPlayerCallbacks;
import com.r0adkll.chipper.ui.widget.EmptyView;
import com.r0adkll.chipper.ui.widget.StickyRecyclerHeadersElevationDecoration;
import com.r0adkll.chipper.utils.UIUtils;
import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.PostOffice;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;

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

    @InjectView(R.id.empty_layout)
    EmptyView mEmptyView;

    @Inject
    ChiptunesPresenter presenter;

    @Inject
    AllChiptuneAdapter adapter;

    @Inject
    Bus mBus;

    private Snackbar mCurrentSnackbar;

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
        adapter.setEmptyView(mEmptyView);
        mChiptuneRecycler.setAdapter(adapter);
        mChiptuneRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        StickyRecyclerHeadersElevationDecoration headersDecor = new StickyRecyclerHeadersElevationDecoration(adapter);
        mChiptuneRecycler.addItemDecoration(headersDecor);
        adapter.setOnItemOptionSelectedListener(this);

        mChiptuneRecycler.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public void onClickFrontView(int position) {
                presenter.onChiptuneSelected(adapter.getItem(position));
            }
        });

        //  Load all chiptunes
        presenter.loadAllChiptunes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if(searchView != null){

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    // Run query
                    adapter.query(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    // Run query
                    adapter.query(s);
                    return true;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    adapter.clearQuery();
                    return true;
                }
            });

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
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
            MusicPlayer.createShufflePlayback(getActivity());
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
    public void refreshContent() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setChiptunes(List<Chiptune> chiptunes) {
        adapter.clear();
        adapter.addAll(chiptunes);

        if(!chiptunes.isEmpty()){
            mChiptuneRecycler.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }else{
            mChiptuneRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

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
    public void showSnackBar(String text) {
        if(mCurrentSnackbar != null) mCurrentSnackbar.dismiss();

        mCurrentSnackbar = Snackbar.with(this)
                .text(text)
                .eventListener(new EventListener() {
                    @Override
                    public void onShow(int i) {
                        float height = Utils.dpToPx(getActivity(), i);

                        // Animate FAB
                        mFABShufflePlay.animate()
                                .translationY(-height)
                                .setDuration(300)
                                .setInterpolator(new DecelerateInterpolator(1.5f))
                                .start();
                    }

                    @Override
                    public void onDismiss(int i) {
                        // Animate FAB
                        mFABShufflePlay.animate()
                                .translationY(0)
                                .setDuration(300)
                                .setInterpolator(new AccelerateInterpolator(1.5f))
                                .start();
                    }
                })
                .attachToRecyclerView(mChiptuneRecycler);

        mCurrentSnackbar.show(this);
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

    /***********************************************************************************************
     *
     * Otto Subscriptions
     *
     */

    @Subscribe
    public void answerOfflineRequestCompletedEvent(OfflineRequestCompletedEvent event){
        adapter.notifyDataSetChanged();
    }

    @DebugLog
    @Subscribe
    public void answerOfflineModeChangeEvent(OfflineModeChangeEvent event){
        adapter.reconcile();
    }

}
