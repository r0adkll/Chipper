package com.r0adkll.chipper.ui.playlists.viewer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
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
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.data.CashMachine;
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
import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.slidableactivity.SlidableAttacher;

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
        setupFab();
        setupRecyclerView();

        // Set the player callbacks
        getPlayer().setCallbacks(this);

        // Setup the loader
        getSupportLoaderManager().initLoader(0, null, this);

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
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_offline:
                presenter.offlinePlaylist(mPlaylist);
                return true;
            case R.id.action_share:
                presenter.sharePlaylist(mPlaylist);
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

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
        adapter.setOnItemClickListener(this);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupFab(){
        // Setup the FAB
        if(!Utils.isLollipop()) {
            ImageView shadow = ButterKnife.findById(mFabPlay, R.id.shadow);
            int dimen = getResources().getDimensionPixelSize(R.dimen.fab_shadow_radius);
            Bitmap blur = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(blur);
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            canvas.drawCircle(dimen / 2f, dimen / 2f, dimen / 2f - Utils.dpToPx(this, 6), p);
            shadow.setImageBitmap(Utils.blurImage(this, blur, 16));
            mFabPlay.setOnClickListener(mFABClickListener);
        }else{

            ViewOutlineProvider vop = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int size = (int) Utils.dpToPx(PlaylistViewerActivity.this, 56);
                    outline.setOval(0, 0, size, size);
                }
            };

            //Button btn = ButterKnife.findById(mFabAdd, R.id.button);
            mFabPlay.setOutlineProvider(vop);
            mFabPlay.setClipToOutline(true);
            mFabPlay.setOnClickListener(mFABClickListener);
        }
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
        if(!chiptunes.isEmpty()){
            mEmptyView.setVisibility(View.GONE);
        }else{
            mEmptyView.setVisibility(View.VISIBLE);
        }

        adapter.clear();
        adapter.addAll(chiptunes);
    }

    @Override
    public void onLoaderReset(Loader<List<ChiptuneReference>> objectLoader) {
        mEmptyView.setVisibility(View.GONE);
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
    public Activity getActivity() {
        return this;
    }

    @Override
    public Playlist getPlaylist() {
        return mPlaylist;
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
}
