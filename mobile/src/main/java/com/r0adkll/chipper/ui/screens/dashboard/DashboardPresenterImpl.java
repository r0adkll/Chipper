package com.r0adkll.chipper.ui.screens.dashboard;

import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.Historian;
import com.r0adkll.chipper.ui.screens.dashboard.model.DashboardCard;
import com.r0adkll.chipper.ui.screens.dashboard.model.MostCompletedServerCard;
import com.r0adkll.chipper.ui.screens.dashboard.model.MostPlayedCard;
import com.r0adkll.chipper.ui.screens.dashboard.model.MostPlayedServerCard;
import com.r0adkll.chipper.ui.screens.dashboard.model.RecentsCard;
import com.r0adkll.chipper.ui.screens.player.MusicPlayer;

import java.util.Arrays;

/**
 * Created by r0adkll on 12/14/14.
 */
public class DashboardPresenterImpl implements DashboardPresenter {

    private ChiptuneProvider mProvider;
    private ChipperService mService;
    private DashboardView mView;

    public DashboardPresenterImpl(DashboardView view,
                                  ChipperService service,
                                  ChiptuneProvider provider){
        mView = view;
        mService = service;
        mProvider = provider;
    }

    @Override
    public void loadDashboardCards() {

        mView.setDashboardCards(
            Arrays.asList(
                new DashboardCard[]{
                    new RecentsCard(mView.getActivity()),
                    new MostPlayedCard(mView.getActivity()),
                    new MostPlayedServerCard(mView.getActivity(), mService, mProvider),
                    new MostCompletedServerCard(mView.getActivity(), mService, mProvider)
                }
            )
        );

    }

    @Override
    public void onChronicleSelected(Historian.Chronicle record) {
        MusicPlayer.createPlayback(mView.getActivity(), record.chiptune);
    }
}
