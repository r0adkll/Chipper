package com.r0adkll.chipper.ui.playlists;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.widget.DividerDecoration;
import com.r0adkll.postoffice.PostOffice;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistActivity extends BaseDrawerActivity implements PlaylistView{

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @InjectView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);
        overridePendingTransition(0, 0);
        getSupportActionBar().setTitle(R.string.navdrawer_item_playlists);

        // Setup the recycler view
//        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
//        adapter.setOnItemClickListener(this);

    }

    /***********************************************************************************************
     *
     *  Helper Methods
     *
     */


    /***********************************************************************************************
     *
     *  View Methods
     *
     */

    @Override
    public void setPlaylists(List<Playlist> playlists) {

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
}
