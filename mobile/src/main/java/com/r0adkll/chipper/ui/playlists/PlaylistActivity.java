package com.r0adkll.chipper.ui.playlists;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.adapters.OnItemClickListener;
import com.r0adkll.chipper.adapters.PlaylistAdapter;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.ui.model.BaseDrawerActivity;
import com.r0adkll.chipper.ui.widget.DividerDecoration;
import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.styles.EditTextStyle;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistActivity extends BaseDrawerActivity implements PlaylistView, LoaderManager.LoaderCallbacks<List<Playlist>>, OnItemClickListener<Playlist> {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @InjectView(R.id.recycle_view)      RecyclerView mRecyclerView;
    @InjectView(R.id.fab_add_playlist)  FrameLayout mFabAdd;

    @Inject
    PlaylistPresenter presenter;

    @Inject
    PlaylistAdapter adapter;

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

        // Setup the FAB
        setupFab();

        // Setup the recycler view
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
        adapter.setOnItemClickListener(this);

        // setup loaders
        getSupportLoaderManager().initLoader(0, null, this);

        // Load shared playlists
        presenter.loadSharedPlaylists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlists, menu);
        return true;
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
}
