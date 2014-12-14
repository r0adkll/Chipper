package com.r0adkll.chipper.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.utils.Tools;

import java.util.List;

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
public class Historian {

    /**
     * Singleton instance
     */
    private static Historian _instance = null;

    /**
     * Singleton accessor to the history archives allowing you to chronicle the user's
     * actions with certain chiptunes
     *
     * @return      the archive singleton instance
     */
    public static Historian getArchive(){
        if(_instance == null) _instance = new Historian();
        return _instance;
    }

    /**
     * Hidden constructor
     */
    private Historian(){}


    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Get a list of chiptunes that were recently played
     *
     * @param limit     the # of chiptunes you want returned (i.e. Last 10)
     * @return          the list of recently played chiptunes
     */
    public List<Chronicle> getRecentlyPlayed(int limit){
        return new Select()
                .from(Chronicle.class)
                .orderBy("last_played DESC")
                .limit(limit)
                .execute();
    }

    /**
     * Get a list of chiptunes that were recently voted
     *
     * @param limit     the # of chiptunes you want returned (i.e. Last 10)
     * @return          the list of recently played chiptunes
     */
    public List<Chronicle> getRecentlyVoted(int limit){
        return new Select()
                .from(Chronicle.class)
                .orderBy("last_voted DESC")
                .limit(limit)
                .execute();
    }

    /**
     * Increment the play count for a given chiptune chronicle
     *
     * @param chiptune      the chiptune to increment the playcount for
     */
    public void incrementPlayCount(Chiptune chiptune){
        Chronicle chronic = getRecord(chiptune);
        chronic.play_count++;
        chronic.last_played = Tools.time();
        chronic.save();
    }

    /**
     * Increment the skipped count for a given chiptune chronicle
     *
     * @param chiptune      the chiptune to increment the skip count for
     */
    public void incrementSkipCount(Chiptune chiptune){
        Chronicle chronic = getRecord(chiptune);
        chronic.skip_count++;
        chronic.save();
    }

    /**
     * Increment the completion count for a given chiptune
     *
     * @param chiptune      the chiptune to increment the completion count for
     */
    public void incrementCompletionCount(Chiptune chiptune){
        Chronicle chronic = getRecord(chiptune);
        chronic.completed_count++;
        chronic.save();
    }

    /**
     * Update the last voting time for a given chiptune
     *
     * @param chiptune      the chiptune to mark the latest vote time for
     */
    public void updateLastVoted(Chiptune chiptune){
        Chronicle chronic = getRecord(chiptune);
        chronic.last_voted = Tools.time();
        chronic.save();
    }

    /**
     * Produce the chronicle to modify. If one doesn't exist for
     * a given chiptune. Create it
     *
     * @param chiptune      the chiptune to get the record for
     * @return              the chiptune's chronicle
     */
    public Chronicle getRecord(Chiptune chiptune){
        Chronicle chronic = fetchChronicle(chiptune);
        if(chronic == null){
            chronic = new Chronicle();
            chronic.chiptune = chiptune;
            chronic.save();
        }
        return chronic;
    }

    /**
     * Get the chronicle for a specific chiptune from the database if it exists.
     *
     * @param chiptune      the chiptune whoes chronicle to get from db
     * @return              the Chronicle, or null if it doesn't exist
     */
    private Chronicle fetchChronicle(Chiptune chiptune){
        return new Select()
                .from(Chronicle.class)
                .where("chiptune=?", chiptune.getId())
                .limit(1)
                .executeSingle();
    }


    /**
     * The permanent record for a given Chiptune and actions enacted upon it
     * by the user. This will be used to track the chiptune's total play count,
     * total skipped count, and total # of times the chiptune has been played to completion.
     *
     * It also tracks the last time the user played it and the last time that they voted on it.
     * This should be sufficient information to provide intuitive feed back to the user on
     * what they've been listening to. It should also allow the app to provided intelligent
     * suggestions.
     */
    @Table(name = "records")
    public static class Chronicle extends Model{

        /**
         * The chiptune reference that this history record pertains to
         */
        @Column
        public Chiptune chiptune;

        /**
         * This is the total # of times the user has played this chiptune
         */
        @Column
        public int play_count;

        /**
         * This is the total # of times the user has skipped on this chiptune, previous or next
         */
        @Column
        public int skip_count;

        /**
         * This is the total # of times the user has completely listened to the chiptune, all
         * the way through
         */
        @Column
        public int completed_count;

        /**
         * This is the last time this chiptune was played
         */
        @Column
        public long last_played;

        /**
         * This was the last time this chiptune was voted upon
         */
        @Column
        public long last_voted;

    }


}
