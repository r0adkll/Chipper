package com.r0adkll.chipper.ui.screens.dashboard;

import com.r0adkll.chipper.data.Historian;

/**
 * Created by r0adkll on 12/14/14.
 */
public interface DashboardPresenter {

    public void loadDashboardCards();

    public void onChronicleSelected(Historian.Chronicle record);

}
