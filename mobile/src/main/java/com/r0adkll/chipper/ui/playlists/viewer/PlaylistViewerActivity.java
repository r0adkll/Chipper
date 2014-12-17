package com.r0adkll.chipper.ui.playlists.viewer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.activeandroid.Model;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.EventListener;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.events.OfflineModeChangeEvent;
import com.r0adkll.chipper.data.events.OfflineRequestCompletedEvent;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.ui.adapters.OnItemClickListener;
import com.r0adkll.chipper.ui.adapters.PlaylistChiptuneAdapter;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.ui.model.BaseActivity;
import com.r0adkll.chipper.ui.player.MusicPlayerCallbacks;
import com.r0adkll.chipper.ui.widget.DividerDecoration;
import com.r0adkll.chipper.ui.widget.EmptyView;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.chipper.utils.UIUtils;
import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.PostOffice;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistViewerActivity extends BaseActivity implements PlaylistViewerView, LoaderManager.LoaderCallbacks<List<ChiptuneReference>>,OnItemClickListener<ChiptuneReference>,MusicPlayerCallbacks {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @InjectView(R.id.recycle_view)  RecyclerView mRecyclerView;
    @InjectView(R.id.empty_layout)  EmptyView mEmptyView;
    @InjectView(R.id.fab_play)      FrameLayout mFabPlay;

    @Inject ChiptuneProvider chiptuneProvider;
    @Inject PlaylistViewerPresenter presenter;
    @Inject PlaylistChiptuneAdapter adapter;
    @Inject Bus mBus;
    @Inject CashMachine mAtm;
    @Inject @CurrentUser
    User mUser;

    @Icicle
    long mPlaylistId = -1;

    // The local playlist reference
    private Playlist mPlaylist;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        // Set the content view of this activity
        setContentView(R.layout.activity_playlist_viewer);
        ButterKnife.inject(this);

        // Load sent playlist to view
        Intent intent = getIntent();
        if(intent != null && mPlaylist == null){
            mPlaylistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, -1);
        }

        // Check the playlist id
        mPlaylist = Model.load(Playlist.class, mPlaylistId);

        // Check to see if we found a playlist, if not kill this activity
        if(mPlaylist == null) finish();

        // Set Title
        getSupportActionBar().setTitle(mPlaylist.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Now present the layout
        UIUtils.setupFAB(this, mFabPlay);
        mFabPlay.setOnClickListener(mFABClickListener);
        setupRecyclerView();

        // Set the player callbacks
        getPlayer().setCallbacks(this);

        // Setup the loader
        getSupportLoaderManager().initLoader(0, null, this);

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if(Utils.isLollipop()){
            finishAfterTransition();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist_viewer, menu);
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

        MenuItem offline = menu.findItem(R.id.action_offline);
        if(mPlaylist.isOffline(mAtm)){
            Drawable icon = getResources().getDrawable(R.drawable.ic_action_cloud_done);
            offline.setIcon(icon);
        }else{
            Drawable icon = getResources().getDrawable(R.drawable.ic_action_cloud_download);
            offline.setIcon(icon);
        }

        // Set the admin submit feature visible or not depending on the user
        MenuItem submitFeature = menu.findItem(R.id.action_submit_feature);
        submitFeature.setVisible(mUser.admin);

        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(Utils.isLollipop()){
                    finishAfterTransition();
                }else {
                    finish();
                }
                return true;
            case R.id.action_offline:
                if(mPlaylist.isOffline(mAtm)){

                    // Delete the cache
                    mAtm.deleteOfflineFiles(new CallbackHandler() {
                        @Override
                        public void onHandle(Object value) {
                            supportInvalidateOptionsMenu();
                        }

                        @Override public void onFailure(String msg) {}
                    }, mPlaylist.getChiptunes(chiptuneProvider));

                }else{
                    presenter.offlinePlaylist(mPlaylist);
                }

                return true;
            case R.id.action_share:
                presenter.sharePlaylist(mPlaylist);
                return true;
            case R.id.action_submit_feature:
                presenter.submitForFeature(mPlaylist);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     *
     *  Helper Methods
     *
     */

    private void setupRecyclerView(){
        adapter.setEmptyView(mEmptyView);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
        adapter.setOnItemClickListener(this);

    }

    /**
     * The floating action button click listener
     */
    private View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.onPlaySelected(mPlaylist);
        }
    };

    @Override
    public void onItemClick(View v, ChiptuneReference item, int position) {
        Chiptune chiptune = chiptuneProvider.getChiptune(item.chiptune_id);
        presenter.onChiptuneSelected(chiptune);
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
        return presenter.getLoader(mPlaylist);
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
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showErrorMessage(String msg) {
        PostOffice.newMail(this)
                .setMessage(msg)
                .show(getFragmentManager());
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
    public Playlist getPlaylist() {
        return mPlaylist;
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
    protected Object[] getModules() {
        return new Object[]{
            new PlaylistViewerModule(this)
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
