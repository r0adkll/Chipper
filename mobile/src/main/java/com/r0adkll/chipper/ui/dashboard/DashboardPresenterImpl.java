package com.r0adkll.chipper.ui.dashboard;

import com.r0adkll.chipper.data.Historian;

import java.util.List;

/**
 * Created by r0adkll on 12/14/14.
 */
public class DashboardPresenterImpl implements DashboardPresenter {

    private static final int RECENTS_LIMIT = 10;

    private DashboardView mView;

    public DashboardPresenterImpl(DashboardView view){
        mView = view;
    }

    @Override
    public void loadRecents() {

        List<Historian.Chronicle> history = Historian.getArchive().getRecentlyPlayed(RECENTS_LIMIT);

    }



}
