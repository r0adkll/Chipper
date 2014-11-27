package com.r0adkll.chipper.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.ui.model.PlaylistStyle;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.styles.EditTextStyle;

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
     * Get the user's favorites playlist, only
     * one should exist
     *
     * @return      the favorites playlist, or null(THIS SHOULD NEVER HAPPEN)
     */
    public Playlist getFavorites(){
        return new Select()
                .from(Playlist.class)
                .where("name=?", "Favorites")
                .and("owner=?", mCurrentUser)
                .limit(1)
                .executeSingle();
    }

    /**
     * Create a new playlist, storing it locally then attempting to create it server side and retroactively
     * updating it if that was a success
     *
     * @param name      the name of the new playlist to create
     * @return          The newly created playlist, or null if a playlist with the same name already exists.
     */
    public Playlist createPlaylist(String name){

        // Absolutely ignore any playlist labeled 'Favorites' as this is a reserved playlist name
        if(name.equalsIgnoreCase("favorites")){
            return null;
        }

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

            // DO NOT DELETE the favorites playlist
            if(playlist.name.equalsIgnoreCase("favorites")) continue;

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
     * Create a dialog prompt to add chiptunes to a library, then add them
     *
     * @param activity      the activity to launch from
     * @param chiptunes     the chiptunes to potentially add
     */
    public void addToPlaylist(final Activity activity, final CallbackHandler cb, final Chiptune... chiptunes){

        // Show playlist selection dialog
        PlaylistStyle style = new PlaylistStyle(activity, mCurrentUser)
                .setOnPlaylistItemSelectedListener(new PlaylistStyle.OnPlaylistItemSelectedListener() {
                    @Override
                    public void onPlaylistSelected(DialogInterface dialog, Playlist playlist) {
                        playlist.add(chiptunes);
                        dialog.dismiss();
                    }

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onAddPlaylistSelected(final DialogInterface dialog) {
                        // Prompt dialog for creating a new playlist
                        // Prompt user for new playlist
                        PostOffice.newMail(activity)
                                .setTitle("New playlist")
                                .setThemeColorFromResource(R.color.primary)
                                .setStyle(new EditTextStyle.Builder(activity)
                                        .setHint("Playlist name")
                                        .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                            @Override
                                            public void onAccepted(String s) {
                                                // Create new playlist object
                                                Playlist playlist = createPlaylist(s);
                                                if(playlist != null) {
                                                    playlist.add(chiptunes);
                                                    cb.onHandle(null);
                                                }else{
                                                    cb.onFailure("Unable to create playlist");
                                                }

                                                // Dismiss the dialog
                                                dialog.dismiss();
                                            }
                                        }).build())
                                .showKeyboardOnDisplay(true)
                                .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.primary)
                                .setButton(Dialog.BUTTON_POSITIVE, "Create", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        cb.onFailure(null);
                                    }
                                })
                                .show(activity.getFragmentManager());

                    }
                });

        // Create Dialog
        PostOffice.newMail(activity)
                .setTitle("Choose a playlist")
                .setStyle(style)
                .show(activity.getFragmentManager());

    }

    /**
     * Add a set of chiptunes to the favorites playlist
     *
     * @param chiptunes     the set of chiptunes to add
     */
    public void addToFavorites(Chiptune... chiptunes){

        Playlist favs = getFavorites();
        if(favs != null){
            favs.add(chiptunes);
        }

    }



}
