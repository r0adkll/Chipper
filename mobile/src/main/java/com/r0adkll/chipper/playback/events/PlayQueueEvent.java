package com.r0adkll.chipper.playback.events;

import com.r0adkll.chipper.playback.model.PlayQueue;

/**
 * Created by r0adkll on 12/3/14.
 */
public class PlayQueueEvent {
    public PlayQueue queue;
    public PlayQueueEvent(PlayQueue queue){
        this.queue = queue;
    }
}
