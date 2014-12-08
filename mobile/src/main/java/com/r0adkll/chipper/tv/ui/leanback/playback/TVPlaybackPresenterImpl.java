package com.r0adkll.chipper.tv.ui.leanback.playback;

import com.r0adkll.chipper.api.model.Chiptune;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.tv.ui.leanback.playlist
 * Created by drew.heavner on 12/8/14.
 */
public class TVPlaybackPresenterImpl implements TVPlaybackPresenter {

    private TVPlaybackView mView;

    /**
     * Constructor
     */
    public TVPlaybackPresenterImpl(TVPlaybackView view){
        mView = view;
    }

    @Override
    public void onChiptuneSelected(Chiptune item) {

    }

    @Override
    public void upvote(Chiptune item) {

    }

    @Override
    public void downvote(Chiptune item) {

    }

    @Override
    public void repeat() {

    }

    @Override
    public void shuffle() {

    }

    @Override
    public void add() {

    }
}
