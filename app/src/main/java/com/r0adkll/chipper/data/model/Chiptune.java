package com.r0adkll.chipper.data.model;

import android.os.Parcel;
import android.os.Parcelable;


import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.concurrent.TimeUnit;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

/**
 * Created by r0adkll on 11/9/14.
 */
@JsonObject
@Table("chiptunes")
public class Chiptune extends Model implements Parcelable{

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @JsonField(name = "id")
    @Column("chiptuneId")
    public String chiptuneId;

    @JsonField
    @Column("title")
    public String title;

    @JsonField
    @Column("artist")
    public String artist;

    @JsonField
    @Column("stream_url")
    public String stream_url;

    @JsonField
    @Column("length")
    public long length;

    /**
     * Default Constructor
     */
    public Chiptune(){}

    /**
     * Parcel Constructor
     * @param in
     */
    private Chiptune(Parcel in){
        super();
        chiptuneId = in.readString();
        title = in.readString();
        artist = in.readString();
        stream_url = in.readString();
        length = in.readLong();
    }

    /***********************************************************************************************
     *
     * Methods
     *
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chiptuneId);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(stream_url);
        dest.writeLong(length);
    }

    /**
     * Parcelable creator class
     */
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

    @Override
    public String toString() {
        return String.format("id: %s\ntitle: %s\nartist: %s\nstream_url: %s\nlength: %d", id, title, artist, stream_url, length);
    }
}
