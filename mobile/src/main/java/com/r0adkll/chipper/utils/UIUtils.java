package com.r0adkll.chipper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.os.Build;
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
