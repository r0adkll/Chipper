package com.r0adkll.chipper.data;

import android.app.Application;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.sync.SyncModule;
import com.r0adkll.chipper.qualifiers.CurrentDevice;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.qualifiers.DeviceId;
import com.r0adkll.chipper.qualifiers.FavoritePlaylist;
import com.r0adkll.chipper.utils.Tools;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hugo.weaving.DebugLog;

/**
 * This module will define all the components used in storing/retreiving saved data
 * components such as playlists and sich
 *
 * Created by r0adkll on 11/11/14.
 */
@Module(
    includes = {
        SyncModule.class
    },
    injects = {
        OfflineIntentService.class
    },
    complete = false,
    library = true
)
public final class DataModule {

    @Provides @Singleton @DeviceId
    String provideDeviceId(Application app){
        return Tools.generateUniqueDeviceId(app);
    }

    @Provides @CurrentUser
    User provideCurrentUser(){
        return new Select()
                .from(User.class)
                .where("is_current_user=?", true)
                .limit(1)
                .executeSingle();
    }

    @Provides @CurrentDevice
    Device provideCurrentDevice(@DeviceId String deviceId){
        return new Select()
                .from(Device.class)
                .where("device_id = ?", deviceId)
                .limit(1)
                .executeSingle();
    }

    @Provides @Singleton
    List<Chiptune> provideAllChiptunes(){
        return new Select()
                .from(Chiptune.class)
                .execute();
    }

    @Provides
    List<Playlist> provideAllPlaylists(){
        return new Select()
                .from(Playlist.class)
                .where("name != ?", "Favorites")
                .execute();
    }

    @Provides @FavoritePlaylist
    Playlist provideFavoritesPlaylist(@CurrentUser User currentUser){
        return new Select()
                .from(Playlist.class)
                .where("name = ?", "Favorites")
                .and("owner = ?", currentUser.getId())
                .limit(1)
                .executeSingle();
    }

    @DebugLog
    @Provides @Singleton
    ChiptuneProvider provideChiptuneProvider(ChipperService service, @CurrentUser User currentUser){
        return new ChiptuneProvider(service, currentUser);
    }

    @Provides @Singleton
    PlaylistManager providePlaylistManager(ChipperService service,
                                           @CurrentUser User user){
        return new PlaylistManager(service, user);
    }

}
