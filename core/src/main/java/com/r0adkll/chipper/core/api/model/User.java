package com.r0adkll.chipper.core.api.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

/**
 * Created by r0adkll on 11/1/14.
 */
@Table(name = "Users")
public class User extends Model implements Parcelable{

    public String id;
    public String email;
    public boolean premium;
    public String public_key;
    public String private_key;



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
