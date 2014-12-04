package com.r0adkll.chipper.ui.model;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.ui.player.MusicPlayer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import dagger.ObjectGraph;

/**
 * This is a base UI activity that assists in creating a scoped
 * object graph on the activity for DI
 *
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui
 * Created by drew.heavner on 11/12/14.
 */
public abstract class BaseActivity extends ActionBarActivity {

    @Optional
    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingLayout;

    protected Toolbar actionBarToolbar;
    private MusicPlayer player;
    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = ChipperApp.get(this).createScopedGraph(getModules());
        activityGraph.inject(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
        ButterKnife.inject(this);
        player = (MusicPlayer) getFragmentManager().findFragmentById(R.id.music_player);
        if(player != null) player.setSlidingLayout(mSlidingLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSlidingLayout.hidePanel();
    }

    /**
     * Get teh reference to the music player interface
     * @return
     */
    protected MusicPlayer getPlayer(){
        return player;
    }

    /**
     * Get the sliding layout reference
     * @return
     */
    protected SlidingUpPanelLayout getSlidingLayout(){
        return mSlidingLayout;
    }

    /**
     * Get the toolbar actionbar
     *
     * @return      get teh action bar
     */
    protected Toolbar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = ButterKnife.findById(this, R.id.toolbar_actionbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return actionBarToolbar;
    }

    protected abstract Object[] getModules();
}
