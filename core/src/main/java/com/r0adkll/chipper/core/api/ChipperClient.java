package com.r0adkll.chipper.core.api;

/**
 * This will be the main chipper api client used to make all the request
 *
 * Created by r0adkll on 11/1/14.
 */
public class ChipperClient {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private ChipperSession session;

    /**
     * Default Constructor
     *
     */
    public ChipperClient(){}

    /**
     * Constructor
     * @param session       the chipper auth session
     */
    public ChipperClient(ChipperSession session){
        this.session = session;
    }

    /***********************************************************************************************
     *
     * API Methods
     *
     */




}
