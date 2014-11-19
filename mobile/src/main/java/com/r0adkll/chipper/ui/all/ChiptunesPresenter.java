package com.r0adkll.chipper.ui.all;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;

/**
 * Created by r0adkll on 11/13/14.
 */
public interface ChiptunesPresenter {

    public void loadAllChiptunes();

    public void onChiptuneSelected(Chiptune chiptune);

    public void upvoteChiptune(Chiptune chiptune);

    public void downvoteChiptune(Chiptune chiptune);

    public void favoriteChiptunes(Chiptune... chiptunes);

    public void addChiptunesToPlaylist(Playlist playlist, Chiptune... chiptunes);

    public void offlineChiptunes(Chiptune... chiptunes);

}
