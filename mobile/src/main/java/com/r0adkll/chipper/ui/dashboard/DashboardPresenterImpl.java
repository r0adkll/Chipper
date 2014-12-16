package com.r0adkll.chipper.ui.dashboard;

import android.content.Intent;

import com.r0adkll.chipper.data.Historian;
import com.r0adkll.chipper.ui.dashboard.model.DashboardCard;
import com.r0adkll.chipper.ui.dashboard.model.MostPlayedCard;
import com.r0adkll.chipper.ui.dashboard.model.RecentsCard;
import com.r0adkll.chipper.ui.player.MusicPlayer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by r0adkll on 12/14/14.
 */
public class DashboardPresenterImpl implements DashboardPresenter {

    private DashboardView mView;

    public DashboardPresenterImpl(DashboardView view){
        mView = view;
    }

    @Override
    public void loadDashboardCards() {

        mView.setDashboardCards(
            Arrays.asList(
                new DashboardCard[]{
                    new RecentsCard(mView.getActivity()),
                    new MostPlayedCard(mView.getActivity())
                }
            )
        );

    }

    @Override
    public void onChronicleSelected(Historian.Chronicle record) {
        Intent playIntent = MusicPlayer.createPlayback(mView.getActivity(), record.chiptune);
        MusicPlayer.startPlayback(mView.getActivity(), playIntent);
    }
}