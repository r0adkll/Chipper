package com.r0adkll.chipper.ui.playlists;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.data.events.OfflineModeChangeEvent;
import com.r0adkll.chipper.data.events.OfflineRequestCompletedEvent;
import com.r0adkll.chipper.ui.adapters.OnItemClickListener;
import com.r0adkll.chipper.ui.adapters.PlaylistAdapter;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.model.SwipeDismissRecyclerViewTouchListener;
import com.r0adkll.chipper.ui.player.MusicPlayerCallbacks;
import com.r0adkll.chipper.ui.widget.DividerDecoration;
import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistActivity extends BaseDrawerActivity implements PlaylistView, LoaderManager.LoaderCallbacks<List<Playlist>>, OnItemClickListener<Playlist>,MusicPlayerCallbacks {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @InjectView(R.id.recycle_view)      SwipeListView mRecyclerView;
    @InjectView(R.id.fab_add_playlist)  FrameLayout mFabAdd;

    @Inject
    PlaylistPresenter presenter;

    @Inject
    PlaylistAdapter adapter;

    @Inject
    Bus mBus;

    private ActionMode mActionMode;

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
        getSupportActionBar().setSubtitle("Last synced at 5:00 PM");

        // Setup the FAB
        setupFab();

        // Set the player callbacks
        getPlayer().setCallbacks(this);

        // Setup the recycler view
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
        mRecyclerView.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public int onChangeSwipeMode(int position) {
                Playlist item = adapter.getItem(position);
                return item.name.equalsIgnoreCase("favorites") ?
                        SwipeListView.SWIPE_MODE_NONE : SwipeListView.SWIPE_MODE_BOTH;
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                List<Playlist> deletedPlaylists = new ArrayList<>();
                for (int position : reverseSortedPositions) {
                    Playlist removed = adapter.removeRaw(position);
                    deletedPlaylists.add(removed);
                }
                adapter.reconcile();
                mRecyclerView.invalidate(); // Force change to get touch controls back

                if(!deletedPlaylists.isEmpty()){
                    presenter.deletePlaylist(deletedPlaylists);
                }

            }

            @Override
            public void onClickFrontView(int position) {
                Playlist item = adapter.getItem(position);
                presenter.onPlaylistSelected(item, position);
            }

            @Override
            public void onMove(int position, float x) {
                View itemView = mRecyclerView.getChildAt(position);
                if(itemView != null) {
                    View leftDel = itemView.findViewById(R.id.left_delete);
                    View rightDel = itemView.findViewById(R.id.right_delete);
                    if (x > 0) {
                        leftDel.setVisibility(View.VISIBLE);
                        rightDel.setVisibility(View.INVISIBLE);
                    } else {
                        leftDel.setVisibility(View.INVISIBLE);
                        rightDel.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onChoiceChanged(int position, boolean selected) {
                if(mActionMode != null){
                    // count item
                }
            }

            @Override
            public void onChoiceStarted() {
                if(mActionMode != null){
                    mActionMode.finish();
                    mActionMode = null;
                }

                mActionMode = startSupportActionMode(mActionModeCallbacks);
            }

            @Override
            public void onChoiceEnded() {
                if(mActionMode != null){
                    mActionMode.finish();
                }
            }
        });

        // setup loaders
        getSupportLoaderManager().initLoader(0, null, this);

        // Load shared playlists
        presenter.loadSharedPlaylists();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlists, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if(searchView != null){

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    // Run query
                    adapter.query(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    // Run query
                    adapter.query(s);
                    return true;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    adapter.clearQuery();
                    return true;
                }
            });

        }
        return super.onPrepareOptionsMenu(menu);
    }

    /***********************************************************************************************
     *
     *  Helper Methods
     *
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupFab(){
        // Setup the FAB
        if(!Utils.isLollipop()) {
            ImageView shadow = ButterKnife.findById(mFabAdd, R.id.shadow);
            int dimen = getResources().getDimensionPixelSize(R.dimen.fab_shadow_radius);
            Bitmap blur = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(blur);
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            canvas.drawCircle(dimen / 2f, dimen / 2f, dimen / 2f - Utils.dpToPx(this, 6), p);
            shadow.setImageBitmap(Utils.blurImage(this, blur, 16));
            mFabAdd.setOnClickListener(mFABClickListener);
        }else{

            ViewOutlineProvider vop = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int size = (int) Utils.dpToPx(PlaylistActivity.this, 56);
                    outline.setOval(0, 0, size, size);
                }
            };

            //Button btn = ButterKnife.findById(mFabAdd, R.id.button);
            mFabAdd.setOutlineProvider(vop);
            mFabAdd.setClipToOutline(true);
            mFabAdd.setOnClickListener(mFABClickListener);
        }
    }

    @Override
    public void onItemClick(View v, Playlist item, int position) {
        presenter.onPlaylistSelected(item, position);
    }


    /**
     * The floating action button click listener
     */
    private View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Prompt user for new playlist
            PostOffice.newMail(PlaylistActivity.this)
                    .setTitle("New playlist")
                    .setThemeColorFromResource(R.color.primary)
                    .setStyle(new EditTextStyle.Builder(PlaylistActivity.this)
                                    .setHint("Playlist name")
                                    .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                        @Override
                                        public void onAccepted(String s) {
                                            // Create a new playlist
                                            presenter.addNewPlaylist(s);
                                        }
                                    }).build())
                    .showKeyboardOnDisplay(true)
                    .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.primary)
                    .setButton(Dialog.BUTTON_POSITIVE, "Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show(getFragmentManager());

        }
    };

    private ActionMode.Callback mActionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_playlist_viewer, menu);
            menu.findItem(R.id.action_search).setVisible(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.action_offline:

                    return true;
                case R.id.action_share:

                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    };

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

    @Override
    public void onStarted() {
        getSlidingLayout().showPanel();
    }

    @Override
    public void onStopped() {
        getSlidingLayout().hidePanel();
    }

    /***********************************************************************************************
     *
     * Otto Subscriptions
     *
     */

    @Subscribe
    public void answerOfflineRequestCompletedEvent(OfflineRequestCompletedEvent event){
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void answerOfflineModeChangeEvent(OfflineModeChangeEvent event){
        adapter.reconcile();
    }
}
