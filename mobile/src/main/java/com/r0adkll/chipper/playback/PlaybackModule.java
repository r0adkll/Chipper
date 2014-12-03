package com.r0adkll.chipper.playback;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;

import com.r0adkll.chipper.ChipperModule;
import com.r0adkll.chipper.playback.model.SessionState;
import com.r0adkll.chipper.prefs.BooleanPreference;
import com.r0adkll.chipper.prefs.IntPreference;
import com.r0adkll.chipper.qualifiers.GenericPrefs;
import com.r0adkll.chipper.qualifiers.SessionRepeatPreference;
import com.r0adkll.chipper.qualifiers.SessionShufflePreference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 11/25/14.
 */
@Module(
    injects = {
        MusicService.class
    },
    complete = false,
    library = true
)
public class PlaybackModule {
    private static final String PREF_SESSION_SHUFFLE = "pref_session_shuffle";
    private static final String PREF_SESSION_REPEAT = "pref_session_repeat";

    @Provides
    AudioPlayer provideAudioPlayer(Application app){
        return new AudioPlayer(app);
    }

    @Provides @Singleton
    MediaSessionManager provideMediaSessionManager(Application app){
        return (MediaSessionManager) app.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    @Provides @Singleton
    AudioManager provideAudioManager(Application app){
        return (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides @Singleton @SessionShufflePreference
    BooleanPreference provideSessionShufflePreference(@GenericPrefs SharedPreferences prefs){
        return new BooleanPreference(prefs, PREF_SESSION_SHUFFLE, false);
    }

    @Provides @Singleton @SessionRepeatPreference
    IntPreference provideSessionRepeatPreference(@GenericPrefs SharedPreferences prefs){
        return new IntPreference(prefs, PREF_SESSION_REPEAT, SessionState.MODE_NONE);
    }


}
