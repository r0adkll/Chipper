package com.r0adkll.chipper.ui.popular;

import android.app.Activity;

import com.r0adkll.chipper.api.model.Chiptune;

import java.util.List;
import java.util.Map;

/**
 * Created by r0adkll on 11/15/14.
 */
public interface PopularView {

    public void setChiptunes(List<Chiptune> chiptunes);

    public void setVoteData(Map<String, Integer> voteData);

    public void showProgress();

    public void hideProgress();

    public void showErrorMessage(String msg);

    public Activity getActivity();

}
