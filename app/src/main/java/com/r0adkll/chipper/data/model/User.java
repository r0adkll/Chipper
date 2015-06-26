package com.r0adkll.chipper.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
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

    @JsonField(name = "id")
    @Column("user_id")
    public String userId;

    @JsonField
    @Column("email")
    public String email;

    @JsonField
    @Column("premium")
    public boolean premium;

    @JsonField
    @Column("admin")
    public boolean admin;

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

    @JsonField
    @Column("is_current_user")
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
        userId = in.readString();
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
