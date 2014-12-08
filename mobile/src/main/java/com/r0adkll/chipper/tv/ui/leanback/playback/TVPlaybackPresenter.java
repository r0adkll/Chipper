package com.r0adkll.chipper.tv.ui.leanback.playback;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.ui.model.IActionPresenter;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.tv.ui.leanback.playlist
 * Created by drew.heavner on 12/8/14.
 */
public interface TVPlaybackPresenter {

    public void onChiptuneSelected(Chiptune item);

    public void upvote(Chiptune item);

    public void downvote(Chiptune item);

    public void repeat();

    public void shuffle();

    public void add();

}
