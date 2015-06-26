package com.r0adkll.chipper.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by r0adkll on 11/16/14.
 */
@Table(name = "featured_playlist_chiptunes", id = BaseColumns._ID)
public class FeaturedChiptuneReference extends Model implements Parcelable {

    /***********************************************************************************************
     *
     * Static Methods
     *
     */

    public static FeaturedChiptuneReference create(FeaturedChiptuneReference ref){
        FeaturedChiptuneReference cr = new FeaturedChiptuneReference();
        cr.chiptune_id = ref.chiptune_id;
        return cr;
    }

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Column(index = true, notNull = true)
    @SerializedName("id")
    public String chiptune_id;

    @Column(
        notNull = true,
        onDelete = Column.ForeignKeyAction.CASCADE,
        onUpdate = Column.ForeignKeyAction.CASCADE
    )
    public FeaturedPlaylist playlist;

    @Column(notNull = true)
    public int sort_order;

    /**
     * Default Constructor
     */
    public FeaturedChiptuneReference(){super();}

    /**
     * Parcel COnstructor
     * @param in
     */
    public FeaturedChiptuneReference(Parcel in){
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

    public static final Creator<FeaturedChiptuneReference> CREATOR = new Creator<FeaturedChiptuneReference>() {
        @Override
        public FeaturedChiptuneReference createFromParcel(Parcel source) {
            return new FeaturedChiptuneReference(source);
        }

        @Override
        public FeaturedChiptuneReference[] newArray(int size) {
            return new FeaturedChiptuneReference[size];
        }
    };
}
