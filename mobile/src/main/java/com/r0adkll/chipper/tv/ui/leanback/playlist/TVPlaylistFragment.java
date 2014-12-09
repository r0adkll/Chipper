package com.r0adkll.chipper.tv.ui.leanback.playlist;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.prefs.BooleanPreference;
import com.r0adkll.chipper.qualifiers.SessionShufflePreference;
import com.r0adkll.chipper.tv.ui.leanback.playback.TVPlaybackActivity;
import com.r0adkll.chipper.tv.ui.model.BaseDetailsFragment;
import com.r0adkll.chipper.tv.ui.model.ChiptunePresenter;
import com.r0adkll.chipper.tv.ui.model.PlaylistDescriptionPresenter;
import com.r0adkll.chipper.ui.player.MusicPlayer;
import com.r0adkll.deadskunk.utils.Utils;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by r0adkll on 12/8/14.
 */
public class TVPlaylistFragment extends BaseDetailsFragment implements TVPlaylistView {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject TVPlaylistPresenter presenter;
    @Inject ChiptuneProvider provider;

    @Inject
    @SessionShufflePreference
    BooleanPreference mShufflePref;

    private Playlist mPlaylist;
    private ArrayObjectAdapter mRowsAdapter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupBackgroundManager();
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if(mPlaylist == null) return;
                if(item instanceof Action){
                    Action action = (Action) item;
                    switch ((int) action.getId()){
                        case 1:
                            MusicPlayer.createTVPlayback(getActivity(), mPlaylist.getChiptunes(provider).get(0), mPlaylist);
                            Intent player = new Intent(getActivity(), TVPlaybackActivity.class);
                            startActivity(player);

                            break;
                        case 2:
                            int i = Utils.getRandom().nextInt(mPlaylist.getCount());
                            Chiptune rnd = mPlaylist.getChiptunes(provider).get(i);

                            mShufflePref.set(true);
                            MusicPlayer.createTVPlayback(getActivity(), rnd, mPlaylist);
                            Intent player1 = new Intent(getActivity(), TVPlaybackActivity.class);
                            startActivity(player1);

                            break;
                        case 3:
                            // do nothing right now
                            break;
                    }
                }else if(item instanceof Chiptune){
                    Chiptune chiptune = (Chiptune) item;
                    MusicPlayer.createTVPlayback(getActivity(), chiptune, mPlaylist);
                    Intent player = new Intent(getActivity(), TVPlaybackActivity.class);
                    startActivity(player);

                }
            }
        });
    }

    /**
     * Setup the background mananager
     */
    private void setupBackgroundManager() {

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());

        Drawable bg = getResources().getDrawable(R.drawable.play_feature_chipper);

        backgroundManager.setDrawable(bg);
    }

    public void setPlaylist(Playlist playlist){
        mPlaylist = playlist;
        buildDetails();
    }

    private void buildDetails() {
        ClassPresenterSelector selector = new ClassPresenterSelector();

        // Attach your media item details presenter to the row presenter:
        DetailsOverviewRowPresenter rowPresenter =
                new DetailsOverviewRowPresenter(new PlaylistDescriptionPresenter());

        selector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        selector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(selector);

        Resources res = getActivity().getResources();
        DetailsOverviewRow detailsOverview = new DetailsOverviewRow(mPlaylist);

        // Add images and action buttons to the details view
        detailsOverview.setImageDrawable(res.getDrawable(R.drawable.chipper_round_watch_bg));
        detailsOverview.addAction(new Action(1, "Play"));
        detailsOverview.addAction(new Action(2, "Shuffle"));
        detailsOverview.addAction(new Action(3, "Share"));
        mRowsAdapter.add(detailsOverview);

        // Add a Related items row
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new ChiptunePresenter());
        List<Chiptune> chiptunes = mPlaylist.getChiptunes(provider);
        listRowAdapter.addAll(0, chiptunes);
        HeaderItem header = new HeaderItem(0, "Chiptunes", null);
        mRowsAdapter.add(new ListRow(header, listRowAdapter));

        setAdapter(mRowsAdapter);
    }

    /***********************************************************************************************
     *
     * View Methods
     *
     */

    @Override
    public void refreshContent() {

    }

    @Override
    public void showSnackBar(String text) {

    }

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new TVPlaylistModule(this)
        };
    }
}
