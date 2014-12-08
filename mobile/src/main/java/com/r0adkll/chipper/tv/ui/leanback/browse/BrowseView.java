package com.r0adkll.chipper.tv.ui.leanback.browse;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.ui.model.IView;

import java.util.List;

/**
 * Created by r0adkll on 12/7/14.
 */
public interface BrowseView extends IView{

    public void setChiptunes(List<Chiptune> chiptunes);

    public void setPlaylists(List<Playlist> playlists);

    public void showErrorMessage(String msg);

    public void showProgress();

    public void hideProgress();

}
