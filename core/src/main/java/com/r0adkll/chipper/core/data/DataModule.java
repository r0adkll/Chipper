package com.r0adkll.chipper.core.data;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Device;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.qualifiers.CurrentDevice;
import com.r0adkll.chipper.core.qualifiers.CurrentUser;
import com.r0adkll.chipper.core.qualifiers.DeviceId;
import com.r0adkll.chipper.core.qualifiers.FavoritePlaylist;
import com.r0adkll.chipper.core.utils.Tools;

import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This module will define all the components used in storing/retreiving saved data
 * components such as playlists and sich
 *
 * Created by r0adkll on 11/11/14.
 */
@Module(
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
    Playlist provideFavoritesPlaylist(User currentUser){
        return new Select()
                .from(Playlist.class)
                .where("name = ?", "Favorites")
                .and("owner = ?", currentUser.getId())
                .limit(1)
                .executeSingle();
    }


}
