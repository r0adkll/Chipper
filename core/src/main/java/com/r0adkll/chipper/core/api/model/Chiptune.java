package com.r0adkll.chipper.core.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

/**
 * Created by r0adkll on 11/9/14.
 */
@Table(name = "Chiptunes")
public class Chiptune extends Model implements Parcelable{

    /***********************************************************************************************
     *
     * Variables
     *
     */

    public String id;
    public String title;
    public String artist;
    public String streamUrl;
    public long length;

    /**
     * Default Constructor
     */
    public Chiptune(){
        super();
    }

    /**
     * Parcel Constructor
     * @param in
     */
    public Chiptune(Parcel in){
        super();
        id = in.readString();
        title = in.readString();
        artist = in.readString();
        streamUrl = in.readString();
        length = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(streamUrl);
        dest.writeLong(length);
    }

    public static final Creator<Chiptune> CREATOR = new Creator<Chiptune>() {
        @Override
        public Chiptune createFromParcel(Parcel source) {
            return new Chiptune(source);
        }

        @Override
        public Chiptune[] newArray(int size) {
            return new Chiptune[size];
        }
    };
}