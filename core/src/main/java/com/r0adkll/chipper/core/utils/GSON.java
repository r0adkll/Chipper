package com.r0adkll.chipper.core.utils;

import com.google.gson.Gson;

/**
 * TODO: Turn this into a Dagger Compatable Singleton
 * Created by r0adkll on 11/2/14.
 */
public class GSON {

    /* Singleton Object */
    private static Gson _gson = null;

    /**
     * Singleton Accessor
     * @return
     */
    public static Gson getGson(){
        if(_gson == null) _gson = new Gson();
        return _gson;
    }

}
