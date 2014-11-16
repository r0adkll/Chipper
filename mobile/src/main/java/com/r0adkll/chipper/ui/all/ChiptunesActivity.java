package com.r0adkll.chipper.ui.all;

import android.app.Activity;
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
import com.r0adkll.chipper.ui.widget.StickyRecyclerHeadersElevationDecoration;
import com.r0adkll.postoffice.PostOffice;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 11/12/14.
 */
public class ChiptunesActivity extends BaseDrawerActivity implements ChiptunesView, AllChiptuneAdapter.OnItemClickListener {

    /***********************************************************************************************
     *
     *  Variables
     *
     */

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
        overridePendingTransition(0, 0);
        getSupportActionBar().setTitle(R.string.navdrawer_item_chiptunes);

        // Setup the adapter with the recycler view
        mAdapter = new AllChiptuneAdapter();
        mChiptuneRecycler.setAdapter(mAdapter);
        mChiptuneRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        StickyRecyclerHeadersElevationDecoration headersDecor = new StickyRecyclerHeadersElevationDecoration(mAdapter);
        mChiptuneRecycler.addItemDecoration(headersDecor);
        mAdapter.setOnItemClickListener(this);

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
