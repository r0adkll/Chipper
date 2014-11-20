package com.r0adkll.chipper.data;

import android.content.ContentResolver;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.qualifiers.CurrentUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/17/14.
 */
@Singleton
public class PlaylistManager  {

    private ChipperService mService;
    private User mCurrentUser;

    @Inject
    public PlaylistManager(ChipperService service, @CurrentUser User user){
        mService = service;
        mCurrentUser = user;
    }

    /**
     * Create a new playlist, storing it locally then attempting to create it server side and retroactively
     * updating it if that was a success
     *
     * @param name      the name of the new playlist to create
     * @return          The newly created playlist, or null if a playlist with the same name already exists.
     */
    public Playlist createPlaylist(String name){

        // Check to see if there are any playlists with this name
        Playlist existing = new Select()
                .from(Playlist.class)
                .where("name = ?", name)
                .executeSingle();

        if(existing == null){

            // Create a new playlist
            Playlist newPlaylist = Playlist.create(name, mCurrentUser);
            long result = newPlaylist.save();

            Timber.i("New Playlist [%s] was created locally = %d", name, result);

            // Return the created playlist
            return newPlaylist;
        }

        return null;
    }

    /**
     * Delete playlists from the server and from the local storage
     *
     * @param playlists     the list of playlists to delete
     */
    public void deletePlaylists(Playlist... playlists){

        // iterate and delete the following playlists
        for(Playlist playlist:playlists){

            // Attempt to delete the server version
            mService.deletePlaylist(mCurrentUser.id, playlist.id, new Callback() {
                @Override public void success(Object o, Response response) {}
                @Override public void failure(RetrofitError error) {}
            });

            // Delete locally
            playlist.delete();
        }

    }
}
