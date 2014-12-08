package com.r0adkll.chipper.tv.ui.model;

import android.app.Activity;
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
public abstract class BaseTVActivity extends Activity {

    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = ChipperApp.get(this).createScopedGraph(getModules());
        activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    protected abstract Object[] getModules();
}
