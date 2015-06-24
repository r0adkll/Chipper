package com.r0adkll.chipper.tv.ui.leanback.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.text.TextUtils;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.tv.ui.leanback.playback.TVPlaybackActivity;
import com.r0adkll.chipper.tv.ui.model.BaseSearchFragment;
import com.r0adkll.chipper.tv.ui.model.ChiptunePresenter;
import com.r0adkll.chipper.ui.screens.player.MusicPlayer;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by r0adkll on 12/8/14.
 */
public class ChipperSearchFragment extends BaseSearchFragment implements ChipperSearchView, SearchFragment.SearchResultProvider, OnItemViewClickedListener {

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
        setOnItemViewClickedListener(this);

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

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        Chiptune chiptune = (Chiptune) item;

        MusicPlayer.createTVPlayback(getActivity(), chiptune);
        Intent player = new Intent(getActivity(), TVPlaybackActivity.class);
        startActivity(player);
    }
}
