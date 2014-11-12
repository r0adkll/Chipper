package com.r0adkll.chipper.core.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by r0adkll on 11/2/14.
 */
@Table(name = "Playlists")
public class Playlist extends Model implements Parcelable{

    public String id;
    public User owner;
    public String name;
    public long updated;
    public User updated_by_user;
    public String token;
    public String permissions;
    public List<Chiptune> tunes;

    /**
     * Default Constructor
     */
    public Playlist(){
        super();
    }

    public Playlist(Parcel in){
        super();
        id = in.readString();
        owner = in.readParcelable(null);
        name = in.readString();
        updated = in.readLong();
        updated_by_user = in.readParcelable(null);
        token = in.readString();
        permissions = in.readString();
        in.readTypedList(tunes, Chiptune.CREATOR);
    }

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
        dest.writeTypedList(tunes);
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
