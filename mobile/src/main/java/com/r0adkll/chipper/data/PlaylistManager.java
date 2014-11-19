package com.r0adkll.chipper.data;

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

    private ExecutorService mExecutor;
    private ChipperService mService;
    private User mCurrentUser;

    @Inject
    public PlaylistManager(ChipperService service, @CurrentUser User user){
        mService = service;
        mCurrentUser = user;
        mExecutor = Executors.newFixedThreadPool(5);
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
            final Playlist newPlaylist = Playlist.create(name, mCurrentUser);
            newPlaylist.save();

            // Now attempt to send this to the server, if successful, return the playlist and
            // update the local reference
            mService.createPlaylist(mCurrentUser.id, newPlaylist.toUpdateMap(), new Callback<Playlist>() {
                @Override
                public void success(Playlist playlist, Response response) {

                    // now that we've successfully updated our playlist from the server, supplement the
                    // id from the local copy to the now updated server copy
                    newPlaylist.update(playlist);

                    // this should trigger updates to listening Loaders in the Playlist UI for the best experience

                }

                @Override
                public void failure(RetrofitError error) {
                    // Silently handle the failure
                    Timber.w(error.getCause(), "Unable to create playlist on the server: %s", error.getLocalizedMessage());
                }
            });

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

    /**
     * Update a playlist after it's content has been changed with the server if possible, this is
     * after local changes have been applied.
     *
     * @param playlist      the playlist to update with the server (that has since changed)
     */
    public void updatePlaylist(final Playlist playlist){

        // Make an API call to update this playlist with the server
        mService.updatePlaylist(mCurrentUser.id, playlist.id, playlist.toUpdateMap(), new Callback<Playlist>() {
            @Override
            public void success(Playlist newPlaylist, Response response) {

                // We have an updated playlist response from the server, update the local reference
                playlist.update(newPlaylist);

            }

            @Override
            public void failure(RetrofitError error) {
                // Silently fail
                Timber.w(error.getCause(), "Unable to update the playlist on the server, please try again later");
            }
        });

    }

    /**
     * Synchronize the local Playlist with the server playlists
     *
     */
    public void synchronize(){

        // 1) Load Server-Side playlists
        mService.getPlaylists(mCurrentUser.id, new Callback<List<Playlist>>() {
            @Override
            public void success(List<Playlist> remote, Response response) {

                // Now load local playlists
                List<Playlist> local = mCurrentUser.getPlaylists();

                // Now start a synchronize task
                SynchronizeTask task = new SynchronizeTask(mService, mCurrentUser, local, remote);
                mExecutor.submit(task);

            }

            @Override
            public void failure(RetrofitError error) {
                // Soft fail
                Timber.w(error.getCause(), "Unable to load playlist from the API.");
            }
        });

    }

    /**
     * This is the synchronizing task that is run in the background on a ExecutorService
     * to synchronize teh local/remote playlist references
     */
    private static class SynchronizeTask implements Runnable{
        private ChipperService service;
        private User user;
        private List<Playlist> local, remote;

        /**
         * Constructor
         * @param local     the the local set of playlists for a user to send
         * @param remote    the remote set of playlists for a user to update
         */
        public SynchronizeTask(ChipperService service, User user, List<Playlist> local, List<Playlist> remote){
            this.service = service;
            this.user = user;
            this.local = local;
            this.remote = remote;
        }


        @Override
        public void run() {

            // TODO: 1) Check for matching playlists and update accordingly

            // Iterate through the local playlists
            for(Playlist localPlaylist: local){

                // Iterate through the remove playlists
                boolean hasRemote = false;
                for(Playlist remotePlaylist: remote){

                    // Check for similar playlists
                    if(localPlaylist.id.equals(remotePlaylist.id)){
                        hasRemote = true;
                        // Ok, found a matching playlist for this local playlist, now determine which
                        // needs to be updated

                        long localTime = localPlaylist.updated;
                        long remoteTime = remotePlaylist.updated;

                        if(localTime > remoteTime){
                            // Local playlist is newer than remote, send update to update it
                            Playlist updatedPlaylist = service.updatePlaylistSync(user.id, localPlaylist.id, localPlaylist.toUpdateMap());
                            if(updatedPlaylist != null)
                                localPlaylist.update(updatedPlaylist);
                        }else if(localTime < remoteTime){
                            // Remote playlist is newer, update the local reference
                            localPlaylist.update(remotePlaylist);
                        }else if(localTime == remoteTime){
                            // These playlists are current, do nothing
                        }

                        break;
                    }

                }

                // 2) Check to see if this local playlist has a remote reference, if not uploaded it
                if(!hasRemote){

                    // Upload to the server and update the local reference with the response
                    Playlist updatedPlaylist = service.updatePlaylistSync(user.id, "new", localPlaylist.toUpdateMap());
                    if(updatedPlaylist != null)
                        localPlaylist.update(updatedPlaylist);

                }

            } // End Part 1 & 2)

            // TODO: 3) Find remote playlists that don't exist locally and synchronize them
            for(Playlist remotePlaylist: remote){

                boolean hasLocal = false;
                for(Playlist localPlaylist: local){
                    if(localPlaylist.id.equals(remotePlaylist.id)){
                        hasLocal = true;
                        break;
                    }
                }

                if(!hasLocal){
                    // Add remote playlist to local

                    // Create a new playlist
                    Playlist newPlaylist = new Playlist();
                    newPlaylist.update(remotePlaylist);
                }

            }

        }
    }


}
