package com.r0adkll.chipper.ui.screens.featured;

import com.r0adkll.chipper.api.model.FeaturedPlaylist;
import com.r0adkll.chipper.ui.model.IView;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface FeaturedView extends IView {

    public void showProgress();

    public void hideProgress();

    public void initializeLoader(FeaturedPlaylist featured);

    public FeaturedPlaylist getFeaturedPlaylist();

}
