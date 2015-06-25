package com.r0adkll.chipper.api.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

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
@JsonObject
@Table("records")
public class Chronicle extends Model {

    /* The Stats Types */
    public static final String PLAY_COUNT = "play";
    public static final String SKIP_COUNT = "skip";
    public static final String COMPLETION_COUNT = "completion";
    public static final String LAST_VOTED = "lastvoted";

    @JsonField
    @Column("record_id")
    public String id;

    /*
     * These are serialized values from GSON after retrieving the object
     * from the network request. These don't need to be saved, however post
     * request the chiptune my need to be fetched
     */
    @JsonField
    public String chiptune_id;

    /**
     * The chiptune reference that this history record pertains to
     */
    @JsonField
    @Column("chiptune")
    public Chiptune chiptune;

    /**
     * This is the total # of times the user has played this chiptune
     */
    @JsonField
    @Column("play_count")
    public int play_count;

    /**
     * This is the total # of times the user has skipped on this chiptune, previous or next
     */
    @JsonField
    @Column("skip_count")
    public int skip_count;

    /**
     * This is the total # of times the user has completely listened to the chiptune, all
     * the way through
     */
    @JsonField
    @Column("completed_count")
    public int completed_count;

    /**
     * This is the last time this chiptune was played
     */
    @JsonField
    @Column("last_played")
    public long last_played;

    /**
     * This was the last time this chiptune was voted upon
     */
    @JsonField
    @Column("last_voted")
    public long last_voted;

    /**
     * The last time this value was updated on the server
     */
    @JsonField
    @Column("updated")
    public long updated;

}