package com.r0adkll.chipper.ui.model;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.r0adkll.chipper.ChipperApp;

import java.util.List;

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
