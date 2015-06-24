package com.r0adkll.chipper.ui.screens.all;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.ui.model.IView;

import java.util.List;

/**
 * Created by r0adkll on 11/13/14.
 */
public interface ChiptunesView extends IView{

    public void setChiptunes(List<Chiptune> chiptunes);

    public void showProgress();

    public void hideProgress();

    public void showErrorMessage(String msg);

}
