package com.r0adkll.chipper.tv.ui.leanback.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.text.TextUtils;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.tv.ui.model.BaseSearchFragment;
import com.r0adkll.chipper.tv.ui.model.ChiptunePresenter;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by r0adkll on 12/8/14.
 */
public class ChipperSearchFragment extends BaseSearchFragment implements ChipperSearchView, SearchFragment.SearchResultProvider {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private ArrayObjectAdapter mRowsAdapter;
    private ChiptunePresenter mChiptunePresenter;

    @Inject
    SearchPresenter presenter;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mChiptunePresenter = new ChiptunePresenter();

        setSearchResultProvider(this);
        //setOnItemViewClickedListener(getDefaultItemClickedListener());

    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        mRowsAdapter.clear();
        if(!TextUtils.isEmpty(newQuery)){
            presenter.search(newQuery);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mRowsAdapter.clear();
        if(!TextUtils.isEmpty(query)){
            presenter.search(query);
        }
        return true;
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new SearchModule(this)
        };
    }

    /***********************************************************************************************
     *
     * View Methods
     *
     */

    @Override
    public void showSearchResults(List<Chiptune> results) {
        ArrayObjectAdapter resultAdapter = new ArrayObjectAdapter(mChiptunePresenter);
        resultAdapter.addAll(0, results);
        mRowsAdapter.add(new ListRow(resultAdapter));
    }

    @Override
    public void showErrorMessage(String msg) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void refreshContent() {

    }

    @Override
    public void showSnackBar(String text) {

    }
}
