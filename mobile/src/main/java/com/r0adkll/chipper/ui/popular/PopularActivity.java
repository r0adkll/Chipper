package com.r0adkll.chipper.ui.popular;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.adapters.PopularChiptuneAdapter;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.widget.DividerDecoration;
import com.r0adkll.postoffice.PostOffice;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by r0adkll on 11/15/14.
 */
public class PopularActivity extends BaseDrawerActivity implements PopularView, PopularChiptuneAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeLayout;

    @InjectView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @Inject
    PopularPresenter presenter;

    @Inject
    PopularChiptuneAdapter adapter;

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
        getSupportActionBar().setTitle(R.string.navdrawer_item_popular);

        // Setup the swipe-to-refresh layout
        mSwipeLayout.setColorSchemeResources(R.color.primary, R.color.primaryDark, R.color.accentColor);
        mSwipeLayout.setOnRefreshListener(this);

        // Setup the recycler view
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
        adapter.setOnItemClickListener(this);

        // Load all chiptunes and vote data
        presenter.loadAllChiptunes();
        presenter.loadVotes();

    }

    /***********************************************************************************************
     *
     *  Helper Methods
     *
     */


    @Override
    public void onItemClick(View v, Chiptune item, int position) {
        presenter.onChiptuneSelected(item);
    }

    @Override
    public void onRefresh() {
        presenter.loadVotes();
    }

    /***********************************************************************************************
     *
     *  View Methods
     *
     */

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setChiptunes(List<Chiptune> chiptunes) {
        adapter.clear();
        adapter.addAll(chiptunes);
    }

    @Override
    public void setVoteData(Map<String, Integer> voteData) {
        adapter.setVoteData(voteData);
    }

    @Override
    public void showProgress() {
        mSwipeLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void showErrorMessage(String msg) {
        PostOffice.newMail(this)
                .setMessage(msg)
                .show(getFragmentManager());
    }

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_POPULAR;
    }

    @Override
    protected void onNavDrawerSlide(float offset) {}

    @Override
    protected Object[] getModules() {
        return new Object[]{
                new PopularModule(this)
        };
    }
}
