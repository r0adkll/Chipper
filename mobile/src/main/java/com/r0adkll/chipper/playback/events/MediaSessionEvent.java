package com.r0adkll.chipper.playback.events;

import android.support.v4.media.session.MediaSessionCompat;

/**
 * This is the class that will be used to transport the media session to/from
 * the service and the player
 *
 * Created by r0adkll on 12/1/14.
 */
public class MediaSessionEvent {

    public MediaSessionCompat session;

    public MediaSessionEvent(MediaSessionCompat session){
        this.session = session;
    }

}
