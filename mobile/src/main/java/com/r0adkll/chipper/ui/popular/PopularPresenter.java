package com.r0adkll.chipper.ui.popular;

import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Playlist;

/**
 * Created by r0adkll on 11/15/14.
 */
public interface PopularPresenter {

    public void loadAllChiptunes();

    public void loadVotes();

    public void onChiptuneSelected(Chiptune chiptune);

    public void upvoteChiptune(Chiptune chiptune);

    public void downvoteChiptune(Chiptune chiptune);

    public void favoriteChiptunes(Chiptune... chiptunes);

    public void addChiptunesToPlaylist(Playlist playlist, Chiptune... chiptunes);

    public void offlineChiptunes(Chiptune... chiptunes);



}
