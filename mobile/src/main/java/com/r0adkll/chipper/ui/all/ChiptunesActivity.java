package com.r0adkll.chipper.ui.all;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.adapters.AllChiptuneAdapter;
import com.r0adkll.chipper.adapters.OnItemClickListener;
import com.r0adkll.chipper.adapters.RecyclerArrayAdapter;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.widget.StickyRecyclerHeadersElevationDecoration;
import com.r0adkll.postoffice.PostOffice;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 11/12/14.
 */
public class ChiptunesActivity extends BaseDrawerActivity
        implements ChiptunesView, OnItemClickListener<Chiptune>,RecyclerArrayAdapter.OnItemOptionSelectedListener<Chiptune> {

    /***********************************************************************************************
     *
     *  Variables
     *
     */

    @InjectView(R.id.chiptune_recycler)
    SwipeListView mChiptuneRecycler;

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
    }


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

}
