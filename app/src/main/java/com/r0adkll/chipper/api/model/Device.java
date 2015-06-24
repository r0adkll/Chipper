package com.r0adkll.chipper.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by r0adkll on 11/2/14.
 */
@Table(name = "Devices")
public class Device extends Model implements Parcelable{

    @Column(name = "dev_id")
    public String id;
    @Column public String device_id;
    @Column public String model;
    @Column public int sdk;
    @Column public boolean tablet;
    @Column public long updated;
    @Column public String push_token;
    @Column public String private_key;
    @Column public String public_key;

    /**
     * Default constructor
     */
    public Device(){
        super();
    }

    public Device(Parcel in){
        super();
        id = in.readString();
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
        if(device.getId() != null) return;

        // Update all it's fields
        id = device.id;
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
        dest.writeString(id);
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
