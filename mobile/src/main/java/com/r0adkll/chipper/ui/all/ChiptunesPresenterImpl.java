package com.r0adkll.chipper.ui.all;

import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Playlist;

/**
 * Created by r0adkll on 11/13/14.
 */
public class ChiptunesPresenterImpl implements ChiptunesPresenter {

    private ChiptunesView mView;
    private ChipperService mService;

    /**
     * Constructor
     *
     * @param view          the chipper view interface
     * @param service       the chipper API service
     */
    public ChiptunesPresenterImpl(ChiptunesView view, ChipperService service){
        mView = view;
        mService = service;
    }

    @Override
    public void onChiptuneSelected(Chiptune chiptune) {

    }

    @Override
    public void upvoteChiptune(Chiptune chiptune) {

    }

    @Override
    public void downvoteChiptune(Chiptune chiptune) {

    }

    @Override
    public void favoriteChiptunes(Chiptune... chiptunes) {

    }

    @Override
    public void addChiptunesToPlaylist(Playlist playlist, Chiptune... chiptunes) {

    }

    @Override
    public void offlineChiptunes(Chiptune... chiptunes) {

    }
}
