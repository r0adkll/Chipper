package com.r0adkll.chipper.tv.ui.leanback.browse;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.tv.ui.leanback.search.ChipperSearchFragment;
import com.r0adkll.chipper.tv.ui.leanback.search.SearchActivity;
import com.r0adkll.chipper.tv.ui.model.BaseBrowseFragment;
import com.r0adkll.chipper.tv.ui.model.ChiptunePresenter;
import com.r0adkll.chipper.tv.ui.model.PlaylistPresenter;
import com.r0adkll.chipper.tv.ui.model.PreferencePresenter;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by r0adkll on 12/7/14.
 */
public class ChipperBrowseFragment extends BaseBrowseFragment implements BrowseView, OnItemViewSelectedListener, OnItemViewClickedListener {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject
    BrowsePresenter presenter;

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private ArrayObjectAdapter mRowsAdapter;
    private ChiptunePresenter mChiptunePresenter;
    private PlaylistPresenter mPlaylistPresenter;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupBackgroundManager();
        setupUIElements();
        setupEventListeners();
        setupRows();
        presenter.loadChiptunes();
        presenter.loadPlaylists();

    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Setup the background mananager
     */
    private void setupBackgroundManager() {

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = getResources().getDrawable(R.drawable.play_feature_chipper);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        backgroundManager.setDrawable(mDefaultBackground);
    }

    /**
     * Setup the browse fragment UI elements
     */
    private void setupUIElements() {
        //setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.app_logo_chipper));
        setTitle(getString(R.string.app_name)); // Badge, when set, takes precedent

        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));

        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    /**
     * Set Event Listeners
     */
    private void setupEventListeners(){
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getActivity(), SearchActivity.class);
                startActivity(search);
            }
        });
    }

    /**
     * Setup the row adapters and sich for this fragment
     */
    private void setupRows(){
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mChiptunePresenter = new ChiptunePresenter();
        mPlaylistPresenter = new PlaylistPresenter();

        HeaderItem chiptunesHeader = new HeaderItem(0, "CHIPTUNES", null);
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(mChiptunePresenter);
        mRowsAdapter.add(new ListRow(chiptunesHeader, adapter));

        HeaderItem playlistHeader = new HeaderItem(1, "PLAYLISTS", null);
        ArrayObjectAdapter playlistAdapter = new ArrayObjectAdapter(mPlaylistPresenter);
        mRowsAdapter.add(new ListRow(playlistHeader, playlistAdapter));

        // Setup the preference presenter
        HeaderItem prefHeader = new HeaderItem(10, "PREFERENCES", null);

        PreferencePresenter mPrefPresenter = new PreferencePresenter();
        ArrayObjectAdapter prefRowAdapter = new ArrayObjectAdapter(mPrefPresenter);
        prefRowAdapter.add(new PreferencePresenter.PreferenceItem(getString(R.string.navdrawer_item_settings), R.drawable.ic_settings));
        prefRowAdapter.add(new PreferencePresenter.PreferenceItem(getString(R.string.navdrawer_item_feedback), R.drawable.ic_forum));
        prefRowAdapter.add(new PreferencePresenter.PreferenceItem(getString(R.string.navdrawer_item_offline), R.drawable.ic_action_cloud_download));
        mRowsAdapter.add(new ListRow(prefHeader, prefRowAdapter));

        setAdapter(mRowsAdapter);

    }

    /***********************************************************************************************
     *
     * Listeners
     *
     */

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if(item instanceof Chiptune) {
            Chiptune chiptune = (Chiptune) item;
            Timber.i("Chiptune selected: %s", chiptune.title);

        }else if(item instanceof PreferencePresenter.PreferenceItem){
            PreferencePresenter.PreferenceItem pref = (PreferencePresenter.PreferenceItem) item;
            Timber.i("Preference %s selected", pref.text);

        }


    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

        if(item instanceof Chiptune) {
            Chiptune chiptune = (Chiptune) item;
            Timber.i("Chiptune clicked: %s", chiptune.title);

        }else if(item instanceof PreferencePresenter.PreferenceItem){
            PreferencePresenter.PreferenceItem pref = (PreferencePresenter.PreferenceItem) item;
            Timber.i("Preference %s clicked", pref.text);

        }

    }


    /***********************************************************************************************
     *
     * View Methods
     *
     */

    @Override
    public void setChiptunes(List<Chiptune> chiptunes) {
        mRowsAdapter.removeItems(0, 1);

        // Create the Row to Show
        HeaderItem chiptunesHeader = new HeaderItem(0, "CHIPTUNES", null);

        // Create the ListRow
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(mChiptunePresenter);
        adapter.addAll(0, chiptunes);
        mRowsAdapter.add(0, new ListRow(chiptunesHeader, adapter));
    }

    @Override
    public void setPlaylists(List<Playlist> playlists) {
        mRowsAdapter.removeItems(1, 1);

        HeaderItem playlistHeader = new HeaderItem(1, "PLAYLISTS", null);
        ArrayObjectAdapter playlistAdapter = new ArrayObjectAdapter(mPlaylistPresenter);
        playlistAdapter.addAll(0, playlists);
        mRowsAdapter.add(1, new ListRow(playlistHeader, playlistAdapter));
    }

    @Override
    public void showErrorMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshContent() {

    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.with(getActivity())
                .text(text)
                .show(getActivity());
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new BrowseModule(this)
        };
    }
}
