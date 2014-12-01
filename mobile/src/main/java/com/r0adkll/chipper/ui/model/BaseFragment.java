package com.r0adkll.chipper.ui.model;

import android.app.Fragment;
import android.os.Bundle;

import com.r0adkll.chipper.ChipperApp;

import dagger.ObjectGraph;

/**
 * Created by r0adkll on 12/1/14.
 */
public abstract class BaseFragment extends Fragment {

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
