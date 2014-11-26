package com.r0adkll.chipper.playback;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;

import com.r0adkll.chipper.ChipperModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 11/25/14.
 */
@Module(
    injects = {
        ChipperModule.class
    },
    complete = false,
    library = true
)
public class PlaybackModule {

    @Provides
    AudioPlayer provideAudioPlayer(Application app){
        return new AudioPlayer(app);
    }

    @Provides @Singleton
    MediaSessionManager provideMediaSessionManager(Application app){
        return (MediaSessionManager) app.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

}
