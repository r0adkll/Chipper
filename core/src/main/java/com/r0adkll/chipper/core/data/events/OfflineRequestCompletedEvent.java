package com.r0adkll.chipper.core.data.events;

import com.r0adkll.chipper.core.api.model.Chiptune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by r0adkll on 11/11/14.
 */
public class OfflineRequestCompletedEvent {

    public List<Chiptune> offline;

    /**
     * Constructor
     */
    public OfflineRequestCompletedEvent(Collection<Chiptune> offlineChiptunes){
        offline = new ArrayList<>(offlineChiptunes);
    }

}
