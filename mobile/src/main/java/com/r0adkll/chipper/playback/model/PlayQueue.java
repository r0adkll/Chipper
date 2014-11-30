package com.r0adkll.chipper.playback.model;

import com.r0adkll.chipper.api.model.Chiptune;

import java.util.List;

/**
 * This object represents the play queue that represents
 * the current play queue during playback. This handles fetching the next tune, shuffling,
 * handling repeat function.-
 *
 * Created by r0adkll on 11/30/14.
 */
public class PlayQueue {


    private int mCurrentIndex;
    private List<Chiptune> mQueue;
    private List<Chiptune> mShuffleQueue;

}
