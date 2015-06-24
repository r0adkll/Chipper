package com.r0adkll.chipper.utils;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.utils
 * Created by drew.heavner on 11/12/14.
 */
public interface CallbackHandler<T> {
    public void onHandle(T value);
    public void onFailure(String msg);
}
