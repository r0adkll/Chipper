package com.r0adkll.chipper.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Playlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by r0adkll on 11/11/14.
 */
public class OfflineRequest implements Parcelable {

    // The chiptunes to download
    List<Chiptune> chiptunes;

    /**
     * Constructor
     */
    private OfflineRequest(){
        chiptunes = new ArrayList<>();
    }

    /**
     * Parcel Constructor
     * @param in
     */
    private OfflineRequest(Parcel in){
        this();
        in.readTypedList(chiptunes, Chiptune.CREATOR);
    }

    /**
     * Get the list of chiptunes to download
     * @return
     */
    public List<Chiptune> getChiptunes(){
        return chiptunes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(chiptunes);
    }

    public static final Creator<OfflineRequest> CREATOR = new Creator<OfflineRequest>() {
        @Override
        public OfflineRequest createFromParcel(Parcel source) {
            return new OfflineRequest(source);
        }

        @Override
        public OfflineRequest[] newArray(int size) {
            return new OfflineRequest[size];
        }
    };

    public static class Builder{

        OfflineRequest request;

        /**
         * Constructor
         */
        public Builder(){
            request = new OfflineRequest();
        }

        public Builder addChiptune(Chiptune chiptune){
            request.chiptunes.add(chiptune);
            return this;
        }

        public Builder addChiptunes(Collection<Chiptune> chiptunes){
            request.chiptunes.addAll(chiptunes);
            return this;
        }

        public Builder addPlaylist(Playlist plist){
            request.chiptunes.addAll(plist.tuneRefs);
            return this;
        }

        public OfflineRequest build(){
            return request;
        }

    }


}
