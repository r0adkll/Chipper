package com.r0adkll.chipper.ui.player;

import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.prefs.BooleanPreference;
import com.r0adkll.chipper.prefs.IntPreference;
import com.r0adkll.chipper.qualifiers.SessionRepeatPreference;
import com.r0adkll.chipper.qualifiers.SessionShufflePreference;
import com.r0adkll.chipper.ui.UIModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by r0adkll on 12/1/14.
 */
@Module(
    injects = MusicPlayer.class,
    addsTo = UIModule.class,
    complete = false

)
public class MusicPlayerModule {

    private MusicPlayerView view;

    public MusicPlayerModule(MusicPlayerView view){
        this.view = view;
    }

    @Provides @Singleton
    MusicPlayerView provideView(){
        return view;
    }

    @Provides @Singleton
    MusicPlayerPresenter providePresenter(MusicPlayerView view,
                                          PlaylistManager playlistManager,
                                          VoteManager voteManager,
                                          @SessionRepeatPreference IntPreference repeatPref,
                                          @SessionShufflePreference BooleanPreference shufflePref){
        return new MusicPlayerPresenterImpl(view, playlistManager, voteManager, repeatPref, shufflePref);
    }

}
