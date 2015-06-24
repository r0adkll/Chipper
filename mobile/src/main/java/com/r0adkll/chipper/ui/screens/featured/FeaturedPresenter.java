package com.r0adkll.chipper.ui.screens.featured;

import com.r0adkll.chipper.api.model.FeaturedChiptuneReference;
import com.r0adkll.chipper.api.model.FeaturedPlaylist;
import com.r0adkll.chipper.data.model.ModelLoader;
import com.r0adkll.chipper.ui.model.IActionPresenter;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface FeaturedPresenter extends IActionPresenter{

    public void loadFromServer();

    public void onPlaySelected(FeaturedPlaylist playlist);

    public void offlinePlaylist(FeaturedPlaylist playlist);

    public void sharePlaylist(FeaturedPlaylist playlist);

    public void favoritePlaylist(FeaturedPlaylist playlist);

    public ModelLoader<FeaturedChiptuneReference> getLoader(FeaturedPlaylist playlist);

}
