package com.r0adkll.chipper.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by r0adkll on 11/16/14.
 */
@Table(name = "playlist_chiptunes")
public class ChiptuneReference extends Model implements Parcelable {

    @Column(index = true)
    @SerializedName("id")
    public String chiptune_id;

    @Column
    public Playlist playlist;

    @Column
    public int sort_order;

    /**
     * Default Constructor
     */
    public ChiptuneReference(){super();}

    /**
     * Parcel COnstructor
     * @param in
     */
    public ChiptuneReference(Parcel in){
        chiptune_id = in.readString();
        sort_order = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chiptune_id);
        dest.writeInt(sort_order);
    }

    public static final Creator<ChiptuneReference> CREATOR = new Creator<ChiptuneReference>() {
        @Override
        public ChiptuneReference createFromParcel(Parcel source) {
            return new ChiptuneReference(source);
        }

        @Override
        public ChiptuneReference[] newArray(int size) {
            return new ChiptuneReference[size];
        }
    };
}
