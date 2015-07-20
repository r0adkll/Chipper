package com.r0adkll.chipper.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.ForeignKey;
import ollie.annotation.NotNull;
import ollie.annotation.Table;

/**
 * A playlists reference to a chiptune to allow for the one-to-many database
 * relations
 *
 * Created by r0adkll on 11/16/14.
 */
@JsonObject
@Table("playlist_chiptunes")
public class ChiptuneReference extends Model implements Parcelable {

    /***********************************************************************************************
     *
     * Static Methods
     *
     */

    public static ChiptuneReference create(ChiptuneReference ref){
        ChiptuneReference cr = new ChiptuneReference();
        cr.chiptuneId = ref.chiptuneId;
        return cr;
    }

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Column("chiptune_id")
    @JsonField(name = "id")
    public String chiptuneId;

    /**
     * The playlist this belongs to
     */
    @Column("playlist")
    @ForeignKey(
        onDelete = ForeignKey.ReferentialAction.CASCADE,
        onUpdate = ForeignKey.ReferentialAction.CASCADE
    )
    @NotNull
    public Playlist playlist;

    /**
     * The Sort order for this item in a given playlist
     */
    @JsonField
    @Column("sort_order")
    public Integer sort_order;

    /**
     * Default Constructor
     */
    public ChiptuneReference(){}

    /**
     * Parcel Constructor
     * @param in
     */
    private ChiptuneReference(Parcel in){
        chiptuneId = in.readString();
        sort_order = in.readInt();
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
        dest.writeString(chiptuneId);
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
