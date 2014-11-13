package com.r0adkll.chipper.ui.all;

import com.r0adkll.chipper.core.api.model.Chiptune;

import java.util.List;

/**
 * Created by r0adkll on 11/13/14.
 */
public interface ChiptunesView {

    public void setChiptunes(List<Chiptune> chiptunes);

    public void showProgress();

    public void hideProgress();

}
