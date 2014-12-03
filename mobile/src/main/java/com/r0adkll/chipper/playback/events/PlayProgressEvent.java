package com.r0adkll.chipper.playback.events;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.playback.events
 * Created by drew.heavner on 12/3/14.
 */
public class PlayProgressEvent {
    public int position, duration;
    public PlayProgressEvent(int pos, int dur){
        position = pos;
        duration = dur;
    }
}
