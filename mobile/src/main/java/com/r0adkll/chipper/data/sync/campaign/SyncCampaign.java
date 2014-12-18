package com.r0adkll.chipper.data.sync.campaign;

import android.content.SyncResult;

import com.r0adkll.chipper.api.ChipperService;
import com.squareup.otto.Bus;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.data.sync.campaign
 * Created by drew.heavner on 12/18/14.
 */
public abstract class SyncCampaign implements Runnable{

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private ChipperService mService;
    private Bus mBus;
    private boolean mIsCanceled = false;
    private SyncResult mSyncResult;

    /**
     * Constructor
     *
     * @param service       the ChipperService used to make all the API calls
     * @param bus           the Otto bus to send events with
     */
    public SyncCampaign(ChipperService service, Bus bus){
        mService = service;
        mBus = bus;
    }

    /**
     * Start the campaign
     *
     * @param result        the sync result to log to
     */
    public void start(SyncResult result){
        mSyncResult = result;
        run();
    }

    /**
     * Cancel the campaign
     */
    public void cancel(){
        mIsCanceled = true;
    }

    /**
     * Get the API Service
     *
     * @return      the chipper api service
     */
    public ChipperService getService(){
        return mService;
    }

    /**
     * Get the otto event bus
     *
     * @return      the event bus
     */
    public Bus getBus(){
        return mBus;
    }

    /**
     * Return whether or not this campaign has been canceled
     */
    public boolean isCanceled(){
        return mIsCanceled;
    }

    /**
     * Get the sync result object for this campaign
     */
    public SyncResult getSyncResult(){
        return mSyncResult;
    }

}