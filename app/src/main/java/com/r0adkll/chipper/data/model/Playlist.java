package com.r0adkll.chipper.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.ftinc.kit.util.Utils;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ollie.Model;
import ollie.Ollie;
import ollie.annotation.Column;
import ollie.annotation.ForeignKey;
import ollie.annotation.Table;
import ollie.query.ResultQueryBase;
import ollie.query.Select;
import rx.Observable;
import rx.Subscriber;

import static ollie.annotation.ForeignKey.ReferentialAction.CASCADE;

/**
 * Created by r0adkll on 11/2/14.
 */
@JsonObject
@Table("playlists")
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

    @JsonField(name = "id")
    @Column("playlist_id")
    public String playlistId;

    /**
     * The owning user who created this playlist
     */
    @JsonField
    @Column("user")
    @ForeignKey(
        onUpdate = CASCADE,
        onDelete = CASCADE
    )
    public User owner;

    /**
     * The name of the playlist designated by the creating user
     */
    @JsonField
    @Column("name")
    public String name;

    /**
     * The time this playlist was last edited in epoch seconds
     */
    @JsonField
    @Column("updated")
    public Long updated;

    /**
     * The last user to have edited this playlist
     */
    @JsonField
    @Column("updated_by_user")
    @ForeignKey(
        onUpdate = CASCADE,
        onDelete = CASCADE
    )
    public User updated_by_user;

    /**
     * The public token generated when this playlist is shared and allows other users to view
     * (and or edit) this playlist
     */
    @JsonField
    @Column("token")
    public String token;

    /**
     * The permissions for this playlist if it is shared, otherwise this will be
     * null and empty
     */
    @JsonField
    @Column("permission")
    public String permissions;

    /**
     * This indicates that this playlist has been deleted from the
     * server via another device and is flagged for removal
     */
    @JsonField
    @Column("deleted")
    public Boolean deleted;

    /**
     * This variable is ONLY sent down when requesting the Featured playlist
     * it is otherwise null with every other playlist
     */
    @JsonField
    @Column("feature_title")
    public String feature_title;

    /**
     * This is only used when GSON auto-deserializes the server response into this list.
     * The Playlist manager is then responsible for saving these references into the database
     * for later use by the function {@link #chiptuneReferences()}
     */
    @JsonField(name = "tunes")
    public List<ChiptuneReference> tuneRefs;

    /**
     * Default Constructor
     */
    public Playlist(){}

    /**
     * Parcelable Constructor
     *
     * @param in        the reconstructing parcelable
     */
    private Playlist(Parcel in){
        playlistId = in.readString();
        owner = in.readParcelable(User.class.getClassLoader());
        name = in.readString();
        feature_title = in.readString();
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
     * Save all the chiptune references to the disk
     */
    public void saveChiptuneReferences(){
        if(tuneRefs != null && !tuneRefs.isEmpty()){
            Ollie.getDatabase().beginTransaction();
            try{
                int N = tuneRefs.size();
                for(int i=0; i<N; i++){
                    ChiptuneReference reference = tuneRefs.get(i);
                    reference.playlist = this;
                    reference.sort_order = i;
                    reference.save();
                }
                Ollie.getDatabase().setTransactionSuccessful();
            }finally{
                Ollie.getDatabase().endTransaction();
            }
        }
    }

    /**
     * The query to get the <i>many</i> chiptune references associated with
     * this playlist
     *
     * @return      the chiptune reference query
     */
    private ResultQueryBase<ChiptuneReference> chiptuneQuery(){
        return Select.from(ChiptuneReference.class)
                .where("playlist=?", id);
    }

    /**
     * Get the list of chiptune references associated with this class
     *
     * @return
     */
    private Observable<List<ChiptuneReference>> chiptuneReferences(){
        return chiptuneQuery()
                .observable()
                .flatMap(chiptuneReferences -> Observable.from(chiptuneReferences))
                .toSortedList((lhs, rhs) -> {
                    return Utils.compare(lhs.sort_order, rhs.sort_order);
                });
    }

    /**
     * TODO: Move to ChiptuneProvider to consolidate
     *
     * Get the list of chiptunes for this given playlist
     *
     * @param provider      the chiptune provider that serves all the chiptunes from memory
     * @return              the list of chiptunes referenced by this playlist
     */
    public Observable<List<Chiptune>> getChiptunes(ChiptuneProvider provider){
        return chiptuneReferences()
                .flatMap(chiptuneReferences -> Observable.from(chiptuneReferences))
                .flatMap(reference -> provider.chiptune(reference.chiptuneId))
                .collect(() -> new ArrayList<>(), (chiptunes, chiptune) -> chiptunes.add(chiptune));
    }

    /**
     * Get the number of chiptunes in this playlist
     *
     * @return      the # of chiptunes in this playlist
     */
    public int count(){
        return Select.columns("COUNT(*)")
                .from(ChiptuneReference.class)
                .where("playlist=?", id)
                .fetchValue(int.class);
    }

    /**
     * Output this playlist to a map that can be used to update the server
     *
     * @return      the map object to update wiht
     */
    public Observable<Map<String, Object>> updateMap(){
        return Observable.create(new Observable.OnSubscribe<Map<String, Object>>() {
                    @Override
                    public void call(Subscriber<? super Map<String, Object>> subscriber) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);

                        if(!TextUtils.isEmpty(feature_title)){
                            map.put("feature_title", feature_title);
                        }

                        // Set the permissions on the playlist if available in the object
                        if(permissions != null && !permissions.isEmpty()){
                            map.put("permission", permissions);
                        }

                        subscriber.onNext(map);
                        subscriber.onCompleted();
                    }
                })
                .zipWith(chiptuneReferences(), (map, references) -> {
                    List<String> tunes = new ArrayList<>();
                    for(ChiptuneReference ref: references){
                        tunes.add(ref.chiptuneId);
                    }

                    map.put("tunes", tunes);
                    return map;
                });

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
        dest.writeString(playlistId);
        dest.writeParcelable(owner, 0);
        dest.writeString(name);
        dest.writeString(feature_title);
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
