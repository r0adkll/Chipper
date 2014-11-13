package com.r0adkll.chipper.core.api.model;

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
