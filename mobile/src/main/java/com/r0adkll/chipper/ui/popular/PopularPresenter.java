package com.r0adkll.chipper.ui.popular;

import com.r0adkll.chipper.ui.model.IActionPresenter;

/**
 * Created by r0adkll on 11/15/14.
 */
public interface PopularPresenter extends IActionPresenter{

    public void loadAllChiptunes();

    public void loadVotes();

}
