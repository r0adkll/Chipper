package com.r0adkll.chipper.playback.events;

import com.r0adkll.chipper.playback.model.PlayQueue;
import com.r0adkll.chipper.playback.model.SessionState;

/**
 * Created by r0adkll on 12/3/14.
 */
public class PlayQueueEvent {
    public PlayQueue queue;
    public SessionState state;
    public PlayQueueEvent(PlayQueue queue, SessionState state){
        this.queue = queue;
        this.state = state;
    }
}
