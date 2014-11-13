package com.r0adkll.chipper.ui.all;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.adapters.AllChiptuneAdapter;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.model.RecyclerItemClickListener;
import com.r0adkll.postoffice.PostOffice;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 11/12/14.
 */
public class ChiptunesActivity extends BaseDrawerActivity implements ChiptunesView, RecyclerItemClickListener.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    /***********************************************************************************************
     *
     *  Variables
     *
     */

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @InjectView(R.id.chiptune_recycler)
    RecyclerView mChiptuneRecycler;

    @Inject
    ChiptunesPresenter presenter;

    private AllChiptuneAdapter mAdapter;


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

        // Setup the swipe to refresh view
        mRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primaryDark, R.color.black);
        mRefreshLayout.setOnRefreshListener(this);

        // Setup the adapter with the recycler view
        mAdapter = new AllChiptuneAdapter();
        mChiptuneRecycler.setAdapter(mAdapter);
        mChiptuneRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mChiptuneRecycler.addItemDecoration(headersDecor);
        mChiptuneRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, this));

        //  Load all chiptunes
        presenter.loadAllChiptunes();
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_CHIPTUNES;
    }

    @Override
    protected void onNavDrawerSlide(float offset) {

    }

    @Override
    public void onItemClick(View view, int position) {
        presenter.onChiptuneSelected(mAdapter.getItem(position));
    }

    @Override
    public void onRefresh() {
        presenter.loadAllChiptunes();
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
    public void setChiptunes(List<Chiptune> chiptunes) {
        mAdapter.clear();
        mAdapter.addAll(chiptunes);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showErrorMessage(String msg) {
        PostOffice.newMail(this)
                .setMessage(msg)
                .show(getSupportFragmentManager());
    }

}
