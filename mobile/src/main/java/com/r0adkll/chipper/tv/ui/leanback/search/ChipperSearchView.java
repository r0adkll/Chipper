package com.r0adkll.chipper.tv.ui.leanback.search;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.ui.model.IView;

import java.util.List;

/**
 * Created by r0adkll on 12/7/14.
 */
public interface ChipperSearchView extends IView{

    public void showSearchResults(List<Chiptune> results);

    public void showErrorMessage(String msg);

    public void showProgress();

    public void hideProgress();

}
