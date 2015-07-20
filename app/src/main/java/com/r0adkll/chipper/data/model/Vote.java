package com.r0adkll.chipper.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

/**
 * This class represent's a user's vote on a particular chiptune
 *
 * Created by r0adkll on 11/15/14.
 */
@JsonObject
@Table("votes")
public class Vote extends Model implements Parcelable{

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final String TYPE_UP = "up";
    public static final String TYPE_DOWN = "down";

    public static final int UP = 1;
    public static final int DOWN = -1;
    public static final int NONE = 0;

    /***********************************************************************************************
     *
     * Columns & Fields
     *
     */

    @JsonField(name = "id")
    @Column("vote_id")
    public String voteId;

    @JsonField
    @Column("tune_id")
    public String tune_id;

    @JsonField
    @Column("value")
    public Integer value;

    @JsonField
    @Column("updated")
    public Long updated;

    /**
     * Default Constructor
     */
    public Vote(){}

    /**
     * Parcel Constructor
     */
    private Vote(Parcel in){
        voteId = in.readString();
        tune_id = in.readString();
        value = in.readInt();
        updated = in.readLong();
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
        dest.writeString(voteId);
        dest.writeString(tune_id);
        dest.writeInt(value);
        dest.writeLong(updated);
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
