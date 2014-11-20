package com.r0adkll.chipper.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.concurrent.TimeUnit;

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

    @Column(name = "chiptune_id")
    public String id;
    @Column public String title;
    @Column public String artist;
    @Column public String streamUrl;
    @Column public long length;

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

    /**
     * Get a formated string representation of this chiptunes play length
     *
     * @return      the formatted length, i.e. 1:23, or 0:23
     */
    public String getFormattedLength(){
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(length),
                TimeUnit.MILLISECONDS.toSeconds(length) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length)));
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
