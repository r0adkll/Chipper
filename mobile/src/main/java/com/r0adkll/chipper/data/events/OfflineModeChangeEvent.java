package com.r0adkll.chipper.data.events;

/**
 * Created by r0adkll on 12/6/14.
 */
public class OfflineModeChangeEvent {
    boolean offlineMode = false;
    public OfflineModeChangeEvent(boolean isOffline){
        offlineMode = isOffline;
    }
}
