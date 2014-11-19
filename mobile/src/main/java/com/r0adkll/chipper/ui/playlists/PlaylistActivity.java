package com.r0adkll.chipper.ui.playlists;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.adapters.PlaylistAdapter;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.widget.DividerDecoration;
import com.r0adkll.postoffice.PostOffice;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistActivity extends BaseDrawerActivity implements PlaylistView, LoaderManager.LoaderCallbacks<List<Playlist>>,PlaylistAdapter.OnItemClickListener {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @InjectView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @Inject
    PlaylistPresenter presenter;

    @Inject
    PlaylistAdapter adapter;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        overridePendingTransition(0, 0);
        getSupportActionBar().setTitle(R.string.navdrawer_item_playlists);

        // Setup the recycler view
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
        adapter.setOnItemClickListener(this);

        // setup loaders
        getSupportLoaderManager().initLoader(0, null, this);

        // Load shared playlists
        presenter.loadSharedPlaylists();
    }

    /***********************************************************************************************
     *
     *  Helper Methods
     *
     */

    @Override
    public void onItemClick(View v, Playlist item, int position) {
        presenter.onPlaylistSelected(item, position);
    }


    /***********************************************************************************************
     *
     *  View Methods
     *
     */

    @Override
    public void setPlaylists(List<Playlist> playlists) {
        adapter.clear();
        adapter.addAll(playlists);
    }

    @Override
    public void setSharedPlaylists(List<Playlist> sharedPlaylists) {

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
                .show(getFragmentManager());
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_PLAYLISTS;
    }

    @Override
    protected void onNavDrawerSlide(float offset) {}

    @Override
    protected Object[] getModules() {
        return new Object[]{
                new PlaylistModule(this)
        };
    }


    /***********************************************************************************************
     *
     * Loader Callbacks
     *
     */

    @Override
    public Loader<List<Playlist>> onCreateLoader(int i, Bundle bundle) {
        return presenter.getLoader();
    }

    @Override
    public void onLoadFinished(Loader<List<Playlist>> playlistLoader, List<Playlist> playlists) {

        // Update the adapter and notify of a data set change
        adapter.clear();
        adapter.addAll(playlists);

    }

    @Override
    public void onLoaderReset(Loader<List<Playlist>> playlistLoader) {

        // Clear adapter and notify of change
        adapter.clear();
    }
}
