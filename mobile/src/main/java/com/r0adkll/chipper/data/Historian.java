package com.r0adkll.chipper.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.r0adkll.chipper.api.model.Chiptune;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This manager is used to keep a record of the user's entire activity. This means their playback
 * history, vote history, playlist history, chiptunes played to completion, chiptunes skipped,
 * chiptune play count.
 *
 * This data will be used to provide beneficial feedback/statistics to the user along with
 * helping curate their Dashboard.
 *
 * Created by r0adkll on 12/13/14.
 */
@Singleton
public class Historian {

    @Inject
    public Historian(){}




    @Table(name = "counts")
    public static class Count extends Model{

        @Column
        public Chiptune chiptune;

        @Column
        public int play_count;

        @Column
        public int skip_count;

        @Column
        public int completed_count;

        @Column
        public long last_played;

    }

    /**
     * This class represents recent events by the user. Such as recently played chiptunes,
     * recently played playlists, recently voted content, recently created playlists
     */
    @Table(name = "recents")
    public static class RecentEvent extends Model {

        /***********************************************************************************************
         *
         * Constants
         *
         */

        public static final int TYPE_CHIPTUNE = 0;
        public static final int TYPE_PLAYLIST = 1;
        public static final int TYPE_VOTE = 2;

        public static final int EVENT_PLAYED = 0;
        public static final int EVENT_CREATED = 1;
        public static final int EVENT_VOTED = 2;


        /***********************************************************************************************
         *
         * Columns
         *
         */

        @Column
        public int type;

        @Column
        public int event;

        @Column(name = "type_id")
        public Long typeId;

        @Column
        public long timestamp;

    }


}
