package com.r0adkll.chipper.ui.screens.dashboard;

import com.r0adkll.chipper.ui.screens.dashboard.model.DashboardCard;
import com.r0adkll.chipper.ui.model.IView;

import java.util.List;

/**
 * Created by r0adkll on 12/14/14.
 */
public interface DashboardView extends IView{

    public void setDashboardCards(List<DashboardCard> cards);

}
