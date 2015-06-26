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
    @Column("chiptune_id")
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
    public Chiptune(Parcel in){
        super();
        chiptuneId = in.readString();
        title = in.readString();
        artist = in.readString();
        stream_url = in.readString();
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
        dest.writeString(chiptuneId);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(stream_url);
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

    @Override
    public String toString() {
        return String.format("id: %s\ntitle: %s\nartist: %s\nstream_url: %s\nlength: %d", id, title, artist, stream_url, length);
    }
}
