package com.r0adkll.chipper.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by r0adkll on 11/2/14.
 */
@Table(name = "Playlists")
public class Playlist extends Model implements Parcelable{

    /***********************************************************************************************
     *
     * Static Methods
     *
     */

    /**
     * Create a new playlist associated with a certain user
     *
     * @param name      the name of the new playlist
     * @param user      the user that created the playlist
     * @return          the new playlist
     */
    public static Playlist create(String name, User user){
        Playlist plist = new Playlist();
        plist.name = name;
        plist.owner = user;
        plist.updated = Tools.time();
        plist.updated_by_user = user;
        plist.token = "";
        plist.permissions = "read";
        return plist;
    }

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Column(name = "playlist_id")
    public String id;

    @Column
    public User owner;

    @Column
    public String name;

    @Column
    public long updated;

    @Column
    public User updated_by_user;

    @Column
    public String token;

    @Column
    public String permissions;

    /*
     * This is only used when GSON auto-deserializes the server response into this list.
     * The Playlist manager is then responsible for saving these references into the database
     * for later use by the function {@link #chiptuneReferences()}
     */
    @SerializedName("tunes")
    public List<ChiptuneReference> tuneRefs;

    /**
     * Default Constructor
     */
    public Playlist(){
        super();
    }

    /**
     * Parcelable Constructor
     *
     * @param in        the reconstructing parcelable
     */
    public Playlist(Parcel in){
        super();
        id = in.readString();
        owner = in.readParcelable(User.class.getClassLoader());
        name = in.readString();
        updated = in.readLong();
        updated_by_user = in.readParcelable(User.class.getClassLoader());
        token = in.readString();
        permissions = in.readString();

        tuneRefs = new ArrayList<>();
        in.readTypedList(tuneRefs, ChiptuneReference.CREATOR);
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Update this playlist from a playlist sent from the API that hasn't add an iD set yet
     *
     * @param playlist      the playlist to update from
     */
    public boolean update(Playlist playlist){
        if(playlist.getId() != null) return false;

        // Update the basic values now
        id = playlist.id;
        name = playlist.name;
        updated = playlist.updated;
        token = playlist.token;
        permissions = playlist.permissions;

        // Update the complex values now
        // 1) Find owner reference
        User _owner = new Select()
                .from(User.class)
                .where("id = ?", playlist.owner.id)
                .and("email = ?", playlist.owner.email)
                .limit(1)
                .executeSingle();

        if(_owner != null){
            owner = _owner;
        }else{
            // Create new owner
            _owner = new User();
            _owner.id = playlist.owner.id;
            _owner.email = playlist.owner.email;
            _owner.save();
            owner = _owner;
        }

        // 2) Find updated_by_user reference
        User _updatedByUser = new Select()
                .from(User.class)
                .where("id = ?", playlist.updated_by_user.id)
                .and("email = ?", playlist.updated_by_user.email)
                .limit(1)
                .executeSingle();

        if(_updatedByUser != null){
            updated_by_user = _updatedByUser;
        }else{
            // Create new user
            _updatedByUser = new User();
            _updatedByUser.id = playlist.updated_by_user.id;
            _updatedByUser.email = playlist.updated_by_user.email;
            _updatedByUser.save();
            updated_by_user = _updatedByUser;
        }

        // 3) Delete all the Chiptune references from the database
        List<ChiptuneReference> references = chiptuneReferences();
        for(ChiptuneReference reference: references){
            reference.delete();
        }

        // now save all the ones from the updated playlist
        tuneRefs = new ArrayList<>(playlist.tuneRefs);
        saveChiptuneReferences();

        // Save ourselves
        save();

        // Return Success
        return true;
    }

    /**
     * Save all the chiptune references to the disk
     */
    public void saveChiptuneReferences(){
        if(tuneRefs != null && !tuneRefs.isEmpty()){
            ActiveAndroid.beginTransaction();
            try{
                int N = tuneRefs.size();
                for(int i=0; i<N; i++){
                    ChiptuneReference reference = tuneRefs.get(i);
                    reference.playlist = this;
                    reference.sort_order = i;
                    reference.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            }finally{
                ActiveAndroid.endTransaction();
            }
        }
    }

    /**
     * Get the list of chiptune references associated with this class
     * @return
     */
    private List<ChiptuneReference> chiptuneReferences(){
        List<ChiptuneReference> references = getMany(ChiptuneReference.class, "playlist");
        if(references != null){
            Collections.sort(references, new Comparator<ChiptuneReference>() {
                @Override
                public int compare(ChiptuneReference lhs, ChiptuneReference rhs) {
                    int lhsSO = lhs.sort_order;
                    int rhsSO = rhs.sort_order;
                    return lhsSO < rhsSO ? -1 : (lhsSO == rhsSO ? 0 : 1);
                }
            });
        }
        return references;
    }

    /**
     * Get all the chiptunes straight from the database
     * @return
     */
    public List<Chiptune> getChiptunes(){
        return getChiptunes(null);
    }

    /**
     * Get the list of chiptunes for this given playlist
     *
     * @param provider      the chiptune provider that serves all the chiptunes from memory
     * @return              the list of chiptunes referenced by this playlist
     */
    public List<Chiptune> getChiptunes(ChiptuneProvider provider){
        List<Chiptune> tunes = new ArrayList<>();
        List<ChiptuneReference> refs = chiptuneReferences();
        for(ChiptuneReference reference: refs){

            // Load the chiptune
            Chiptune chiptune = null;
            if(provider != null){
                chiptune = provider.getChiptune(reference.chiptune_id);
            }else{
                chiptune = new Select().from(Chiptune.class)
                        .where("chiptune_id = ?", reference.chiptune_id)
                        .limit(1)
                        .executeSingle();
            }

            if(chiptune != null){
                tunes.add(chiptune);
            }
        }
        return tunes;
    }

    /**
     * Add chiptune reference associated with this
     *
     * @param tune
     */
    public void add(Chiptune tune){
        // Generate ChiptuneReference and add it to this playlist
        ChiptuneReference reference = new ChiptuneReference();
        reference.chiptune_id = tune.id;
        reference.playlist = this;
        reference.sort_order = chiptuneReferences().size();
        reference.save();

        // Update this playlists updated time
        updated = Tools.time();
        save();
    }

    /**
     *
     * @param tune
     * @return
     */
    public boolean remove(Chiptune tune){
        // Get Chiptune Reference, and delete it from the database, then update the playlist to the server
        ChiptuneReference reference = new Select()
                .from(ChiptuneReference.class)
                .where("chiptune_id = ?", tune.id)
                .and("playlist = ?", this)
                .limit(1)
                .executeSingle();

        if(reference != null){
            reference.delete();
            avengeSortOrder(chiptuneReferences());

            // Update this playlists updated time
            updated = Tools.time();
            save();
            return true;
        }

        return false;
    }

    /**
     * Re-arrange a chiptune in this playlist to it's new index
     *
     * @param tune      the chiptune to move
     * @param index     the index to move it to
     * @return          the results
     */
    public boolean rearrange(Chiptune tune, int index){

        // first, find chiptunes old reference
        ChiptuneReference reference = new Select()
                .from(ChiptuneReference.class)
                .where("chiptune_id = ?", tune.id)
                .and("playlist = ?", this)
                .limit(1)
                .executeSingle();

        if(reference != null){

            // Now iterate through the items after this new insert and update there sort order accordingly
            List<ChiptuneReference> references = chiptuneReferences();
            references.remove(reference);
            references.add(index, reference);
            avengeSortOrder(references);

            // Update this playlists updated time
            updated = Tools.time();
            save();
            return true;
        }

        return false;
    }

    /**
     * Reset the 'sort_order' on all the chiptune references for this playlist
     * in order to properly set their sort order
     *
     * @param references
     */
    private void avengeSortOrder(List<ChiptuneReference> references){
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < references.size(); i++) {
                ChiptuneReference ref = references.get(i);
                ref.sort_order = i;
                ref.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally{
            ActiveAndroid.endTransaction();
        }
    }

    /**
     * Output this playlist to a map that can be used to update the server
     *
     * @return      the map object to update wiht
     */
    public Map<String, Object> toUpdateMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);

        // Set the permissions on the playlist if available in the object
        if(permissions != null && !permissions.isEmpty()){
            map.put("permission", permissions);
        }

        List<ChiptuneReference> references = chiptuneReferences();
        List<String> tunes = new ArrayList<>();
        for(ChiptuneReference ref: references){
            tunes.add(ref.chiptune_id);
        }

        map.put("tunes", tunes);
        return map;
    }

    /***********************************************************************************************
     *
     * Parcelable Methods
     *
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(owner, 0);
        dest.writeString(name);
        dest.writeLong(updated);
        dest.writeParcelable(updated_by_user, 0);
        dest.writeString(token);
        dest.writeString(permissions);
        dest.writeTypedList(tuneRefs);
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

}