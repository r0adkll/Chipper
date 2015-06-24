package com.r0adkll.chipper.ui.model;

import com.r0adkll.chipper.api.model.Chiptune;

/**
 * Created by r0adkll on 11/27/14.
 */
public interface IActionPresenter {

    public void onChiptuneSelected(Chiptune chiptune);

    public void upvoteChiptune(Chiptune chiptune);

    public void downvoteChiptune(Chiptune chiptune);

    public void favoriteChiptunes(Chiptune... chiptunes);

    public void addChiptunesToPlaylist(Chiptune... chiptunes);

    public void offlineChiptunes(Chiptune... chiptunes);

}
