package com.r0adkll.chipper.ui.featured;

import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.model.ModelLoader;
import com.r0adkll.chipper.ui.model.IActionPresenter;

/**
 * Created by r0adkll on 11/16/14.
 */
public interface FeaturedPresenter extends IActionPresenter{

    public void loadFromServer();

    public void onPlaySelected(Playlist playlist);

    public void offlinePlaylist(Playlist playlist);

    public void sharePlaylist(Playlist playlist);

    public ModelLoader<ChiptuneReference> getLoader(Playlist playlist);

}
