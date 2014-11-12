package com.r0adkll.chipper.core.utils;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.utils
 * Created by drew.heavner on 11/12/14.
 */
public interface Callback<T> {
    public void onHandle(T value);
    public void onFailure(String msg);
}
