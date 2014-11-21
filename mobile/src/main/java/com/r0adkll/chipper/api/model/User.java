package com.r0adkll.chipper.api.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by r0adkll on 11/1/14.
 */
@Table(name = "Users")
public class User extends Model implements Parcelable{

    @Column(name = "userId")
    public String id;

    @Column
    public String email;

    @Column
    public boolean premium;

    /**
     * Do note that this is only valid for registering a new device, after the device is created
     * these tokens are invalidated
     */
    @Column
    public String public_key;

    /**
     * Do note that this is only valid for registering a new device, after the device is created
     * these tokens are invalidated
     */
    @Column
    public String private_key;

    @Column(name = "is_current_user")
    public boolean isCurrentUser = false;


    /**
     * Default Constructor
     */
    public User(){
        super();
    }

    /**
     * Parcel Constructor
     *
     * @param in    The parcel input
     */
    public User(Parcel in){
        super();
        id = in.readString();
        email = in.readString();
        premium = in.readInt() == 0 ? false : true;
        public_key = in.readString();
        private_key = in.readString();
        isCurrentUser = in.readInt() == 1 ? true : false;
    }

    /**
     * Clear the session keypair and save this
     */
    public void clearSession(){
        public_key = "";
        private_key = "";
        save();
    }

    /**
     * Get a list of playlists for this user
     *
     * @return  return the list of playlists associated with this user
     */
    public List<Playlist> getPlaylists(){
        return getMany(Playlist.class, "owner");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeInt(premium ? 1 : 0);
        dest.writeString(public_key);
        dest.writeString(private_key);
        dest.writeInt(isCurrentUser ? 1 : 0);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
