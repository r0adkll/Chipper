package com.r0adkll.chipper.data.sync.campaign;

import android.text.TextUtils;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.squareup.otto.Bus;

import java.util.List;

import retrofit.RetrofitError;
import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.data.sync
 * Created by drew.heavner on 11/18/14.
 */
public class PlaylistCampaign extends SyncCampaign{

    /**
     * Create a new sync campaign
     */
    public PlaylistCampaign(ChipperService service, Bus bus){
        super(service, bus);
    }

    /**
     * TODO: Modify 'Model.java' object to create a .save() method that doesn't notify
     * TODO: the content provider so we don't chain a rediculous # sync requests
     *
     * Run the campaign
     */
    @Override
    public void run() {
        // Ensure that the sync result has been set
        if(getSyncResult() == null) return;

        Timber.i("Beginning Sync Campaign");

        // Get Logged in user
        User user = new Select()
                .from(User.class)
                .where("is_current_user=?", true)
                .limit(1)
                .executeSingle();

        // If we found the existing user, run the sync
        if(user != null) {

            Timber.i("Starting Sync for User [%s, %s]", user.email, user.id);

            // Get the local and remote playlists
            List<Playlist> remote = getService().getPlaylistsSync(user.id);
            List<Playlist> local = user.getPlaylists();

            if(remote != null){

                // Iterate through the local playlists
                for(Playlist localPlaylist: local){
                    if(isCanceled()) return;
                    if(!TextUtils.isEmpty(localPlaylist.feature_title)) continue;

                    // Check to see if the playlists remote id is set
                    boolean hasRemote = false;
                    if(localPlaylist.id != null || localPlaylist.name.equalsIgnoreCase(Playlist.FAVORITES)) {

                        // Iterate through the remove playlists
                        for (Playlist remotePlaylist : remote) {
                            if (isCanceled()) return;

                            // Build cases
                            boolean idCheck = localPlaylist.id != null ? localPlaylist.id.equals(remotePlaylist.id) : false;
                            boolean favoritesCheck = localPlaylist.name.equalsIgnoreCase(Playlist.FAVORITES) &&
                                    remotePlaylist.name.equalsIgnoreCase(Playlist.FAVORITES);
                            boolean areEqual = idCheck || favoritesCheck;

                            // Check for similar playlists
                            if (areEqual && !remotePlaylist.deleted) {
                                hasRemote = true;
                                // Ok, found a matching playlist for this local playlist, now determine which
                                // needs to be updated

                                long localTime = localPlaylist.updated;
                                long remoteTime = remotePlaylist.updated;

                                if (localTime > remoteTime) {
                                    // First check if local playlist has been deleted
                                    if(!localPlaylist.deleted) {

                                        // Local playlist is newer than remote, send update to update it
                                        Playlist updatedPlaylist = getService().updatePlaylistSync(user.id, localPlaylist.id, localPlaylist.toUpdateMap());
                                        if (updatedPlaylist != null) {
                                            localPlaylist.update(updatedPlaylist);
                                            getSyncResult().stats.numUpdates++;
                                            Timber.d("Local playlist [%s] newer than remote, uploading...", localPlaylist.name);
                                        } else {
                                            getSyncResult().stats.numSkippedEntries++;
                                        }

                                    }else{

                                        // Local has been deleted, so delete it from the server
                                        try {
                                            getService().deletePlaylistSync(user.id, localPlaylist.id);
                                            localPlaylist.delete();
                                            getSyncResult().stats.numDeletes++;
                                            Timber.d("Local playlist [%s] is newer, but is deleted. Deleting from server!", localPlaylist.name);
                                        }catch (RetrofitError e){
                                            Timber.e(e, "Unable to delete local playlist: %s", e.getLocalizedMessage());
                                            getSyncResult().stats.numSkippedEntries++;
                                        }

                                    }

                                } else if (localTime < remoteTime) {
                                    // Remote playlist is newer, update the local reference
                                    localPlaylist.update(remotePlaylist);
                                    getSyncResult().stats.numUpdates++;
                                    Timber.d("Remote playlist [%s] is newer than local, downloading...", remotePlaylist.name);
                                } else if (localTime == remoteTime) {
                                    // These playlists are current, do nothing
                                }

                                break;
                            } else if (idCheck && remotePlaylist.deleted) {
                                hasRemote = true;

                                // Remote playlist has been deleted, so delete the local reference too
                                localPlaylist.delete();
                                getSyncResult().stats.numDeletes++;
                                Timber.d("Local playlist [%s] was deleted since the remote was marked as deleted", localPlaylist.name);
                                break;
                            }

                        }
                    }

                    // 2) Check to see if this local playlist has a remote reference, if not uploaded it
                    if(!hasRemote){

                        if(!localPlaylist.deleted) {

                            // Upload to the server and update the local reference with the response
                            Playlist updatedPlaylist = getService().updatePlaylistSync(user.id, "new", localPlaylist.toUpdateMap());
                            if (updatedPlaylist != null) {
                                localPlaylist.update(updatedPlaylist);
                                getSyncResult().stats.numUpdates++;
                                Timber.d("Playlist [%s] not found on server, uploading...", localPlaylist.name);
                            } else {
                                getSyncResult().stats.numSkippedEntries++;
                            }

                        }else{
                            localPlaylist.delete();
                            getSyncResult().stats.numDeletes++;
                            Timber.d("Local playlist [%s] wasn't found on the server, and is deleted, removing from system.", localPlaylist.name);
                        }

                    }

                } // End Part 1 & 2)

                // 3) Find remote playlists that don't exist locally and synchronize them
                for(Playlist remotePlaylist: remote){
                    if(isCanceled()) return;

                    boolean hasLocal = false;
                    for(Playlist playlist: local){
                        if(playlist.id.equals(remotePlaylist.id)){
                            hasLocal = true;
                            break;
                        }
                    }

                    if(!hasLocal && !remotePlaylist.deleted){
                        // Add remote playlist to local
                        Timber.d("Remote playlist found that doesn't exist locally, %s", remotePlaylist.name);

                        // Create a new playlist
                        Playlist newPlaylist = new Playlist();
                        newPlaylist.save();
                        newPlaylist.update(remotePlaylist);
                        getSyncResult().stats.numUpdates++;
                    }

                }

            }
        }

        Timber.i("Playlist Sync Complete");
    }

}