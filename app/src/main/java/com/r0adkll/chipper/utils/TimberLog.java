package com.r0adkll.chipper.utils;

import retrofit.RestAdapter;
import timber.log.Timber;

/**
 * Created by r0adkll on 6/25/15.
 */
public class TimberLog implements RestAdapter.Log {
    @Override
    public void log(String message) {
        Timber.v(message);
    }
}
