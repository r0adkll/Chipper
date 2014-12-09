package com.r0adkll.chipper.tv.ui.model;

import android.os.Bundle;
import android.support.v17.leanback.app.PlaybackOverlayFragment;

import com.r0adkll.chipper.ChipperApp;

import dagger.ObjectGraph;

/**
 * Created by r0adkll on 12/8/14.
 */
public abstract class BasePlaybackOverlayFragment extends PlaybackOverlayFragment {
    private ObjectGraph fragmentGraph;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentGraph = ChipperApp.get(getActivity()).createScopedGraph(getModules());
        fragmentGraph.inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentGraph = null;
    }

    protected abstract Object[] getModules();
}
