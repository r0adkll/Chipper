package com.r0adkll.chipper.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

/**
 * Created by r0adkll on 11/1/14.
 */
@JsonObject
@Table("users")
public class User extends Model implements Parcelable{

    /***********************************************************************************************
     *
     * Columns & Fields
     *
     */

    @JsonField(name = "id")
    @Column("user_id")
    public String userId;

    /**
     * The user's email address
     */
    @JsonField
    @Column("email")
    public String email;

    /**
     * This flag indicates that the user has upgraded their account to premium using in-app
     * premium
     */
    @JsonField
    @Column("premium")
    public Boolean premium;

    /**
     * This flag indicates that the user has used Chipper before the major '2.0.0' re-re-haul
     * to Material Goodness
     */
    @JsonField
    @Column("legacy")
    public Boolean legacy;

    /**
     * This flags the user as an Administrator. An Elite few that have special powers in the
     * realm of chipper.
     */
    @JsonField
    @Column("admin")
    public Boolean admin;

    /**
     * Do note that this is only valid for registering a new device, after the device is created
     * these tokens are invalidated
     */
    @JsonField
    @Column("public_key")
    public String public_key;

    /**
     * Do note that this is only valid for registering a new device, after the device is created
     * these tokens are invalidated
     */
    @JsonField
    @Column("private_key")
    public String private_key;

    /**
     * This value is set when the user logs in to differentiate from other user objects
     */
    @JsonIgnore
    @Column("is_current_user")
    public Boolean isCurrentUser = false;

    /**
     * Default Constructor
     */
    public User(){}

    /**
     * Parcel Constructor
     *
     * @param in    The parcel input
     */
    private User(Parcel in){
        userId = in.readString();
        email = in.readString();
        premium = in.readInt() == 0 ? false : true;
        public_key = in.readString();
        private_key = in.readString();
        isCurrentUser = in.readInt() == 1 ? true : false;
    }

    /***********************************************************************************************
     *
     * Methods
     *
     */

    /**
     * Clear the session keypair and save this
     */
    public void clearSession(){
        public_key = "";
        private_key = "";
        save();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
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
