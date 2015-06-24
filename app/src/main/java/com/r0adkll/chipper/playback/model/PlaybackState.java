package com.r0adkll.chipper.playback.model;

import java.text.DecimalFormat;

/**
 * This class is used to indicate the current playback state of the music service
 *
 * Created by r0adkll on 11/26/14.
 */
public class PlaybackState {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    private final DecimalFormat MIN_FORMAT = new DecimalFormat("#0");
    private final DecimalFormat SEC_FORMAT = new DecimalFormat("00");

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private boolean mIsPlaying = false;
    private int mProgress = -1;
    private int mTotal = -1;
    private float mBuffer = 0;

    /**
     * Constructor
     */
    public PlaybackState(){}

    /***********************************************************************************************
     *
     * Getters and Setters
     *
     */

    public boolean isPlaying(){
        return mIsPlaying;
    }

    public void setIsPlaying(boolean val){
        mIsPlaying = val;
    }

    public int getProgress(){
        return mProgress;
    }

    public void setProgress(int val){
        mProgress = val;
    }

    public int getTotal(){
        return mTotal;
    }

    public void setTotal(int val){
        mTotal = val;
    }

    public float getBufferPercentage(){
        return mBuffer;
    }

    public void setBufferPercentage(float percent){
        mBuffer = percent;
    }

    /***********************************************************************************************
     *
     * Helper Functions
     *
     */

    public boolean isDisabled(){
        return mProgress == -1 && mTotal == -1;
    }

    public boolean isIndeterminate(){
        return mProgress == 0 && mTotal == 0;
    }

    public float getPlayPercentage(){
        return ((float)mProgress) / ((float)mTotal);
    }

    public String getFormattedProgressTime(){
        int secProg = (mProgress / 1000);
        int minProg = secProg / 60;
        return String.format("%s:%s", MIN_FORMAT.format(minProg), SEC_FORMAT.format(secProg % 60));
    }

    public String getFormattedTotalTime(){
        int secTotal = (mTotal / 1000);
        int minTotal = secTotal / 60;
        return String.format("%s:%s", MIN_FORMAT.format(minTotal), SEC_FORMAT.format(secTotal % 60));
    }


}
