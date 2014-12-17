package com.r0adkll.chipper.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.r0adkll.chipper.R;
import com.r0adkll.deadskunk.utils.Utils;

import butterknife.ButterKnife;

/**
 * Created by r0adkll on 11/13/14.
 */
public class UIUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startActivityWithTransition(Activity activity,
                                                   Intent intent,
                                                   final View clickedView,
                                                   final String transitionName) {
        ActivityOptions options = null;
        if (Utils.isLollipop() && clickedView != null && !TextUtils.isEmpty(transitionName)) {
            options = ActivityOptions
                    .makeSceneTransitionAnimation(activity, clickedView, transitionName);
        }

        activity.startActivity(intent, (options != null) ? options.toBundle() : null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startActivityWithTransition(Activity activity,
                                                   Intent intent,
                                                   Pair<View, String>... transitions){
        ActivityOptions options = null;
        if (Utils.isLollipop() && transitions != null) {
            options = ActivityOptions
                    .makeSceneTransitionAnimation(activity, transitions);
        }

        activity.startActivity(intent, (options != null) ? options.toBundle() : null);
    }

    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }


    @SuppressLint("NewApi")
    public static void setupFAB(final Context ctx, FrameLayout fab){
        // Setup the FAB
        if(!Utils.isLollipop()) {
            ImageView shadow = ButterKnife.findById(fab, R.id.shadow);
            int dimen = ctx.getResources().getDimensionPixelSize(R.dimen.fab_shadow_radius);
            Bitmap blur = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(blur);
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            canvas.drawCircle(dimen / 2f, dimen / 2f, dimen / 2f - Utils.dpToPx(ctx, 6), p);
            shadow.setImageBitmap(Utils.blurImage(ctx, blur, 16));
        }else{

            ViewOutlineProvider vop = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int size = (int) Utils.dpToPx(ctx, 56);
                    outline.setOval(0, 0, size, size);
                }
            };

            //Button btn = ButterKnife.findById(mFabAdd, R.id.button);
            fab.setOutlineProvider(vop);
            fab.setClipToOutline(true);
        }
    }

}
