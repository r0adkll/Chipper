package com.r0adkll.chipper.utils;

import android.os.Build;
import android.view.View;

/**
 * Created by r0adkll on 11/13/14.
 */
public class UIUtils {

    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }

}
