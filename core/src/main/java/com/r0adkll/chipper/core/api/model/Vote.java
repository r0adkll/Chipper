package com.r0adkll.chipper.core.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by r0adkll on 11/15/14.
 */
@Table(name = "Votes")
public class Vote extends Model implements Parcelable{

    @Column(name = "tune_id")
    public String id;

    @Column
    public int vote;


    /**
     * Default Constructor
     */
    public Vote(){ super(); }

    /**
     * Parcel Constructor
     */
    public Vote(Parcel in){
        id = in.readString();
        vote = in.readInt();
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
        dest.writeInt(vote);
    }

    public static final Creator<Vote> CREATOR = new Creator<Vote>() {
        @Override
        public Vote createFromParcel(Parcel source) {
            return new Vote(source);
        }

        @Override
        public Vote[] newArray(int size) {
            return new Vote[size];
        }
    };

}
