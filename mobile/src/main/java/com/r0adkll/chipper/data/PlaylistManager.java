package com.r0adkll.chipper.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.activeandroid.query.Select;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.ui.model.PlaylistStyle;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.chipper.utils.Tools;
import com.r0adkll.deadskunk.utils.IntentUtils;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.styles.EditTextStyle;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
                .where("name=?", Playlist.FAVORITES)
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
        if(name.equalsIgnoreCase(Playlist.FAVORITES) ||
                name.equalsIgnoreCase(Playlist.FEATURED) ||
                TextUtils.isEmpty(name)){
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
    public Collection<Playlist> deletePlaylists(Playlist... playlists){
        return deletePlaylists(Arrays.asList(playlists));
    }

    public Collection<Playlist> deletePlaylists(Collection<Playlist> playlists){
        // iterate and delete the following playlists
        for(Playlist playlist:playlists){

            // DO NOT DELETE the favorites playlist
            if(Playlist.FAVORITES.equalsIgnoreCase(playlist.name)) continue;

            // Delete locally
            playlist.deleted = true;
            playlist.updated = Tools.time();
            //playlist.save();
        }

        return playlists;
    }

    /**
     * Create a dialog prompt to add chiptunes to a library, then add them
     *
     * @param activity      the activity to launch from
     * @param chiptunes     the chiptunes to potentially add
     */
    public void addToPlaylist(final Activity activity, final CallbackHandler<Playlist> cb, final Chiptune... chiptunes){

        // Show playlist selection dialog
        PlaylistStyle style = new PlaylistStyle(activity, mCurrentUser)
                .setOnPlaylistItemSelectedListener(new PlaylistStyle.OnPlaylistItemSelectedListener() {
                    @Override
                    public void onPlaylistSelected(DialogInterface dialog, Playlist playlist) {
                        if(playlist.add(chiptunes)){
                            cb.onHandle(playlist);
                        }else{
                            cb.onFailure(String.format("Chiptune%s already exist in the playlist", chiptunes.length > 1 ? "s" : ""));
                        }
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
                                                    if(playlist.add(chiptunes)){
                                                        cb.onHandle(playlist);
                                                    }else{
                                                        cb.onFailure(String.format("Chiptune%s already exist in the playlist", chiptunes.length > 1 ? "s" : ""));
                                                    }
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
    public boolean addToFavorites(Chiptune... chiptunes){

        Playlist favs = getFavorites();
        if(favs != null){
            if(favs.add(chiptunes)) {
                Timber.i("%d chiptunes added to favorites", chiptunes.length);
                return true;
            }
        }

        return false;
    }

    /**
     * Return whether or not a chiptune is contained in
     * the favorites playlist
     *
     * @param chiptune      the chiptune to check
     * @return
     */
    public boolean isFavorited(Chiptune chiptune){
        Playlist favs = getFavorites();
        if(favs != null){
            return favs.contains(chiptune);
        }
        return false;
    }

    /**
     * Share a playlist
     *
     * @param playlist
     * @param permission
     */
    public void sharePlaylist(Playlist playlist, String permission, final CallbackHandler<String> cbh){
        if(playlist.token == null || playlist.token.isEmpty()) {
            mService.sharePlaylist(mCurrentUser.id, playlist.id, permission, new Callback<Map<String, String>>() {
                @Override
                public void success(Map<String, String> shareResponse, Response response) {
                    // get the link
                    String link = shareResponse.get("link");

                    // Prompt to share the link
                    cbh.onHandle(link);
                }

                @Override
                public void failure(RetrofitError error) {
                    cbh.onFailure(error.getLocalizedMessage());
                }
            });

        }

    }

}
