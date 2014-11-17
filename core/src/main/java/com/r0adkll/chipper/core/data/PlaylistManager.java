package com.r0adkll.chipper.core.data;

import com.r0adkll.chipper.core.api.ChipperService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by r0adkll on 11/17/14.
 */
@Singleton
public class PlaylistManager  {

    private ChipperService mService;

    @Inject
    public PlaylistManager(ChipperService service){
        mService = service;
    }



}
