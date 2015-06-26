package com.r0adkll.chipper.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

/**
 * Created by r0adkll on 11/2/14.
 */
@JsonObject
@Table("devices")
public class Device extends Model implements Parcelable{

    @JsonField(name = "id")
    @Column("dev_id")
    public String _id;

    @JsonField
    @Column("device_id")
    public String device_id;

    @JsonField
    @Column("model")
    public String model;

    @JsonField
    @Column("sdk")
    public Integer sdk;

    @JsonField
    @Column("tablet")
    public Boolean tablet;

    @JsonField
    @Column("updated")
    public Long updated;

    @JsonField
    @Column("push_token")
    public String push_token;

    @JsonField
    @Column("private_key")
    public String private_key;

    @JsonField
    @Column("public_key")
    public String public_key;

    /**
     * Default constructor
     */
    public Device(){}

    private Device(Parcel in){
        super();
        _id = in.readString();
        device_id = in.readString();
        model = in.readString();
        sdk = in.readInt();
        tablet = in.readInt() == 0 ? false : true;
        updated = in.readLong();
        push_token = in.readString();
        private_key = in.readString();
        public_key = in.readString();
    }

    /**
     * Update this device with a model device Gson'd from the server
     *
     * @param device        the device to update with
     */
    public void update(Device device){
        if(device.id != null) return;

        // Update all it's fields
        _id = device._id;
        device_id = device.device_id;
        model = device.model;
        sdk = device.sdk;
        tablet = device.tablet;
        updated = device.updated;
        push_token = device.push_token;

        // DON'T UPDATE THE KEYPAIR HERE, THAT IS ONLY SET FROM THE INITIAL RESPONSE.

        // Save to disk
        save();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(device_id);
        dest.writeString(model);
        dest.writeInt(sdk);
        dest.writeInt(tablet ? 1 : 0);
        dest.writeLong(updated);
        dest.writeString(push_token);
        dest.writeString(private_key);
        dest.writeString(public_key);
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel source) {
            return new Device(source);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

}
