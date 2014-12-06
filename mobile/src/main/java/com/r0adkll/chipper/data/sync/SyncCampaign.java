package com.r0adkll.chipper.data.sync;

import android.content.SyncResult;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;

import java.util.List;

import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.data.sync
 * Created by drew.heavner on 11/18/14.
 */
public class SyncCampaign implements Runnable{

    /**
     * Campaign factory interface for creating dynamic sync campaign construction factories
     *
     */
    public interface Factory{

        /**
         * Create a campaign for a sync result
         * @param syncResult    the result to create a campaign for
         * @return              the sync campaign
         */
        SyncCampaign create(ChipperService service, SyncResult syncResult);

    }

    /***********************************************************************************************
     *
     * Variables
     *
     */


    private ChipperService mService;
    private final SyncResult mSyncResult;
    private boolean mIsCanceled = false;

    /**
     * Constructor for a campaign
     *
     * @param result        the sync result
     */
    public SyncCampaign(ChipperService service, SyncResult result){
        mService = service;
        mSyncResult = result;
    }


    /**
     * Run the campaign
     */
    @Override
    public void run() {

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
            List<Playlist> remote = mService.getPlaylistsSync(user.id);
            List<Playlist> local = user.getPlaylists();

            if(remote != null){

                // Iterate through the local playlists
                for(Playlist localPlaylist: local){
                    if(mIsCanceled) return;

                    // Iterate through the remove playlists
                    boolean hasRemote = false;
                    for(Playlist remotePlaylist: remote){
                        if(mIsCanceled) return;

                        // Check for similar playlists
                        if(localPlaylist.id.equals(remotePlaylist.id)){
                            hasRemote = true;
                            // Ok, found a matching playlist for this local playlist, now determine which
                            // needs to be updated

                            long localTime = localPlaylist.updated;
                            long remoteTime = remotePlaylist.updated;

                            if(localTime > remoteTime){
                                // Local playlist is newer than remote, send update to update it
                                Playlist updatedPlaylist = mService.updatePlaylistSync(user.id, localPlaylist.id, localPlaylist.toUpdateMap());
                                if(updatedPlaylist != null) {
                                    localPlaylist.update(updatedPlaylist);
                                    mSyncResult.stats.numUpdates++;
                                    Timber.i("Local playlist [%s] newer than remote, uploading...", localPlaylist.name);
                                }else{
                                    mSyncResult.stats.numSkippedEntries++;
                                }
                            }else if(localTime < remoteTime){
                                // Remote playlist is newer, update the local reference
                                localPlaylist.update(remotePlaylist);
                                mSyncResult.stats.numUpdates++;
                                Timber.i("Remote playlist [%s] is newer than local, downloading...", remotePlaylist.name);
                            }else if(localTime == remoteTime){
                                // These playlists are current, do nothing
                            }

                            break;
                        }

                    }

                    // 2) Check to see if this local playlist has a remote reference, if not uploaded it
                    if(!hasRemote){

                        // Upload to the server and update the local reference with the response
                        Playlist updatedPlaylist = mService.updatePlaylistSync(user.id, "new", localPlaylist.toUpdateMap());
                        if(updatedPlaylist != null) {
                            localPlaylist.update(updatedPlaylist);
                            mSyncResult.stats.numUpdates++;
                            Timber.i("Playlist [%s] not found on server, uploading...", localPlaylist.name);
                        }else{
                            mSyncResult.stats.numSkippedEntries++;
                        }

                    }

                } // End Part 1 & 2)

                // TODO: 3) Find remote playlists that don't exist locally and synchronize them
                for(Playlist remotePlaylist: remote){
                    if(mIsCanceled) return;

                    boolean hasLocal = false;
                    for(Playlist localPlaylist: local){
                        if(localPlaylist.id.equals(remotePlaylist.id)){
                            hasLocal = true;
                            break;
                        }
                    }

                    if(!hasLocal){
                        // Add remote playlist to local
                        Timber.i("Remote playlist found that doesn't exist locally, %s", remotePlaylist.name);

                        // Create a new playlist
                        Playlist newPlaylist = new Playlist();
                        newPlaylist.save();
                        newPlaylist.update(remotePlaylist);
                        mSyncResult.stats.numUpdates++;
                    }

                }

            }
        }
    }

    /**
     * Cancel the campaign
     */
    public void cancel(){
        mIsCanceled = true;
    }

}