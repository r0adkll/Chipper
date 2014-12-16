package com.r0adkll.chipper.ui.featured;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.activeandroid.Model;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.nispok.snackbar.Snackbar;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.events.OfflineModeChangeEvent;
import com.r0adkll.chipper.data.events.OfflineRequestCompletedEvent;
import com.r0adkll.chipper.ui.adapters.FeaturedChiptuneAdapter;
import com.r0adkll.chipper.ui.adapters.OnItemClickListener;
import com.r0adkll.chipper.ui.adapters.PlaylistChiptuneAdapter;
import com.r0adkll.chipper.ui.adapters.RecyclerArrayAdapter;
import com.r0adkll.chipper.ui.model.BaseActivity;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.player.MusicPlayerCallbacks;
import com.r0adkll.chipper.ui.widget.DividerDecoration;
import com.r0adkll.chipper.ui.widget.EmptyView;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.chipper.utils.UIUtils;
import com.r0adkll.postoffice.PostOffice;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

/**
 * Created by r0adkll on 11/16/14.
 */
public class FeaturedActivity extends BaseDrawerActivity implements
        FeaturedView,
        LoaderManager.LoaderCallbacks<List<ChiptuneReference>>,
        OnItemClickListener<ChiptuneReference>,
        MusicPlayerCallbacks, RecyclerArrayAdapter.OnItemOptionSelectedListener<ChiptuneReference> {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public final SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd-yyyy 'at' h:mm a");

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @InjectView(R.id.recycle_view)  SwipeListView mRecyclerView;
    @InjectView(R.id.empty_layout)  EmptyView mEmptyView;
    @InjectView(R.id.fab_play)      FrameLayout mFabPlay;

    @Inject ChiptuneProvider chiptuneProvider;
    @Inject PlaylistManager playlistManager;
    @Inject FeaturedPresenter presenter;
    @Inject FeaturedChiptuneAdapter adapter;
    @Inject Bus mBus;
    @Inject CashMachine mAtm;

    @Icicle
    long mFeaturedId = -1;

    // The local playlist reference
    private Playlist mFeaturedPlaylist;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        Icepick.restoreInstanceState(this, savedInstanceState);

        // Set the content view of this activity
        setContentView(R.layout.activity_featured);
        ButterKnife.inject(this);

        if(mFeaturedId != -1) {

            // Check the playlist id
            mFeaturedPlaylist = Model.load(Playlist.class, mFeaturedId);
            getSupportLoaderManager().initLoader(0, null, this);
            getSupportActionBar().setTitle(mFeaturedPlaylist.name);

        }

        // Set Title
        getSupportActionBar().setTitle(Playlist.FEATURED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Now present the layout
        UIUtils.setupFAB(this, mFabPlay);
        mFabPlay.setOnClickListener(mFABClickListener);
        setupRecyclerView();

        // Set the player callbacks
        getPlayer().setCallbacks(this);

        // Setup the loader
        presenter.loadFromServer();

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_featured, menu);
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

        if(mFeaturedPlaylist != null) {
            MenuItem offline = menu.findItem(R.id.action_offline);
            if (mFeaturedPlaylist.isOffline(mAtm)) {
                offline.setIcon(R.drawable.ic_action_cloud_done);
            } else {
                offline.setIcon(R.drawable.ic_action_cloud_download);
            }

            MenuItem favorite = menu.findItem(R.id.action_favorite);
            if(mFeaturedPlaylist.isFavorited(chiptuneProvider, playlistManager)){
                favorite.setIcon(R.drawable.ic_action_favorite);
            }else{
                favorite.setIcon(R.drawable.ic_action_favorite_outline);
            }

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_offline:
                if(mFeaturedPlaylist.isOffline(mAtm)){

                    // Delete the cache
                    mAtm.deleteOfflineFiles(new CallbackHandler() {
                        @Override
                        public void onHandle(Object value) {
                            adapter.notifyDataSetChanged();
                            supportInvalidateOptionsMenu();
                            showSnackBar(String.format("%d chiptunes were deleted from the disk",
                                            mFeaturedPlaylist.getCount()));
                        }

                        @Override public void onFailure(String msg) {}
                    }, mFeaturedPlaylist.getChiptunes(chiptuneProvider));

                }else{
                    presenter.offlinePlaylist(mFeaturedPlaylist);
                }

                return true;
            case R.id.action_share:
                presenter.sharePlaylist(mFeaturedPlaylist);
                return true;
            case R.id.action_favorite:
                presenter.favoritePlaylist(mFeaturedPlaylist);
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     *
     *  Helper Methods
     *
     */

    /**
     * Setup the recycler view
     */
    private void setupRecyclerView(){
        adapter.setEmptyView(mEmptyView);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
        mRecyclerView.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public void onClickFrontView(int position) {
                ChiptuneReference reference = adapter.getItem(position);
                Chiptune chiptune = chiptuneProvider.getChiptune(reference.chiptune_id);
                presenter.onChiptuneSelected(chiptune);
            }
        });
        adapter.setOnItemOptionSelectedListener(this);
    }

    /**
     * The floating action button click listener
     */
    private View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.onPlaySelected(mFeaturedPlaylist);
        }
    };

    @Override
    public void onItemClick(View v, ChiptuneReference item, int position) {
        Chiptune chiptune = chiptuneProvider.getChiptune(item.chiptune_id);
        presenter.onChiptuneSelected(chiptune);
    }

    @Override
    public void onSelected(View view, ChiptuneReference reference) {
        Chiptune item = chiptuneProvider.getChiptune(reference.chiptune_id);
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
        mRecyclerView.closeOpenedItems();
    }

    @Override
    public void onStarted() {
        getSlidingLayout().showPanel();
    }

    @Override
    public void onStopped() {
        getSlidingLayout().hidePanel();
    }

    /***********************************************************************************************
     *
     * Loader Callbacks
     *
     */

    @Override
    public Loader<List<ChiptuneReference>> onCreateLoader(int i, Bundle bundle) {
        return presenter.getLoader(mFeaturedPlaylist);
    }

    @Override
    public void onLoadFinished(Loader<List<ChiptuneReference>> objectLoader, List<ChiptuneReference> chiptunes) {
        adapter.clear();
        adapter.addAll(chiptunes);
    }

    @Override
    public void onLoaderReset(Loader<List<ChiptuneReference>> objectLoader) {
        adapter.clear();
    }

    /***********************************************************************************************
     *
     *  View Methods
     *
     */


    @Override
    public void initializeLoader(Playlist featured) {
        mFeaturedPlaylist = featured;
        mFeaturedId = mFeaturedPlaylist.getId();

        getSupportActionBar().setTitle(mFeaturedPlaylist.feature_title);
//        getSupportActionBar().setSubtitle(String.format("Updated on %s", mDateFormat.format(new Date(mFeaturedPlaylist.updated * 1000))));
        getSupportActionBar().setSubtitle(Playlist.FEATURED);
        supportInvalidateOptionsMenu();

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void showProgress() {
        mEmptyView.setLoading(true);
    }

    @Override
    public void hideProgress() {
        mEmptyView.setLoading(false);
    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.with(this)
                .text(text)
                .show(this);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Playlist getFeaturedPlaylist() {
        return mFeaturedPlaylist;
    }

    @Override
    public void refreshContent() {
        adapter.notifyDataSetChanged();
    }

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_FEATURED;
    }

    @Override
    protected void onNavDrawerSlide(float offset) {

    }

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new FeaturedModule(this)
        };
    }

    /***********************************************************************************************
     *
     * Otto Subscriptions
     *
     */

    @Subscribe
    public void answerOfflineRequestCompletedEvent(OfflineRequestCompletedEvent event){
        adapter.notifyDataSetChanged();
        supportInvalidateOptionsMenu();
    }

    @Subscribe
    public void answerOfflineModeChangeEvent(OfflineModeChangeEvent event){
        adapter.reconcile();
    }

}
