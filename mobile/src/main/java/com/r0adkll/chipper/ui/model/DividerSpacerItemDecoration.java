package com.r0adkll.chipper.ui.model;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerSpacerItemDecoration extends RecyclerView.ItemDecoration {

    private boolean mDrawFirst = false;
    private float mHeight = -1;

    public DividerSpacerItemDecoration(float height, boolean drawFirst) {
        mHeight = height;
        mDrawFirst = drawFirst;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mHeight == -1) {
            return;
        }
        if (parent.getChildPosition(view) < 1 && !mDrawFirst) {
            return;
        }
 
        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            outRect.top = (int) mHeight;
        } else {
            outRect.left = (int) mHeight;

            if(parent.getChildPosition(view) == parent.getChildCount()-1){
                outRect.right = (int) mHeight;
            }

        }
    }
 
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
 
    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        } else {
            throw new IllegalStateException(
                    "DividerItemDecoration can only be used with a LinearLayoutManager.");
        }
    }
}