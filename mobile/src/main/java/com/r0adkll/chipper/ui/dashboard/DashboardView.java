package com.r0adkll.chipper.ui.dashboard;

import com.r0adkll.chipper.data.Historian;
import com.r0adkll.chipper.ui.dashboard.model.DashboardCard;
import com.r0adkll.chipper.ui.model.IView;

import java.util.List;

/**
 * Created by r0adkll on 12/14/14.
 */
public interface DashboardView extends IView{

    public void setDashboardCards(List<DashboardCard> cards);

}
