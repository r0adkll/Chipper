package com.r0adkll.chipper.ui.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.ui.adapters.DragInterface;
import com.r0adkll.deadskunk.utils.Utils;

import butterknife.ButterKnife;

public class DragController implements RecyclerView.OnItemTouchListener {
    public static final int ANIMATION_DURATION = 100;
    private RecyclerView recyclerView;
    private ImageView overlay;
    private int handleId;

    private boolean isDragging = false;
    private View draggingView;
    private boolean isFirst = true;
    private long draggingId = -1;
    private float startY = 0f;
    private Rect startBounds = null;

    private int startPos;

    public DragController(final RecyclerView recyclerView, ImageView overlay, int handleId) {
        this.recyclerView = recyclerView;
        this.overlay = overlay;
        this.handleId = handleId;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (isDragging) {
            return true;
        }

        // if event is action down
        if(e.getActionMasked() == MotionEvent.ACTION_DOWN){

            // Find The child view's handle
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            View handle = ButterKnife.findById(childView, handleId);

            // Check for handle collision
            Rect rect = new Rect();
            handle.getHitRect(rect);
            int[] childViewCoords = new int[2];
            childView.getLocationOnScreen(childViewCoords);
            int x = (int) e.getRawX() - childViewCoords[0];
            int y = (int) e.getRawY() - childViewCoords[1];

            if(rect.contains(x, y)){
                // Initialize draging
                isDragging = true;
                dragStart(e.getX(), e.getY());
                return true;
            }

        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        int y = (int) e.getY();
        if (e.getAction() == MotionEvent.ACTION_UP) {
            dragEnd();
            isDragging = false;
        } else {
            drag(y);
        }
    }

    @SuppressLint("NewApi")
    private void dragStart(float x, float y) {
        DragInterface adapter = (DragInterface) recyclerView.getAdapter();
        draggingView = recyclerView.findChildViewUnder(x, y);
        startPos = recyclerView.getChildPosition(draggingView);
        adapter.onDragStarted(startPos);
        View first = recyclerView.getChildAt(0);
        isFirst = draggingView == first;
        startY = y - draggingView.getTop();
        paintViewToOverlay(draggingView);
        overlay.setTranslationY(y - startY);
        draggingView.setVisibility(View.INVISIBLE);
        draggingId = recyclerView.getChildItemId(draggingView);
        startBounds = new Rect(draggingView.getLeft(), draggingView.getTop(), draggingView.getRight(), draggingView.getBottom());

        overlay.setBackgroundColor(recyclerView.getResources().getColor(R.color.background_material_light));
        if(Utils.isLollipop()){
            overlay.setElevation(Utils.dpToPx(recyclerView.getContext(), 4));
        }

    }

    private void drag(int y) {
        overlay.setTranslationY(y - startY);
        if (!isInPreviousBounds()) {
            View view = recyclerView.findChildViewUnder(0, y);
            if (recyclerView.getChildPosition(view) != 0 && view != null) {
                swapViews(view);
            }
        }
    }

    private void swapViews(View current) {
        long replacementId = recyclerView.getChildItemId(current);
        DragInterface adapter = (DragInterface) recyclerView.getAdapter();
        int start = adapter.getPositionForId(replacementId);
        int end = adapter.getPositionForId(draggingId);
        adapter.moveItem(start, end);
        if (isFirst) {
            recyclerView.scrollToPosition(end);
            isFirst = false;
        }
        startBounds.top = current.getTop();
        startBounds.bottom = current.getBottom();
    }

    @SuppressLint("NewApi")
    private void dragEnd() {
        overlay.setImageBitmap(null);
        draggingView.setVisibility(View.VISIBLE);
        float translationY = overlay.getTranslationY();
        draggingView.setTranslationY(translationY - startBounds.top);
        draggingView.animate().translationY(0f).setDuration(ANIMATION_DURATION).start();

        overlay.setBackground(null);
        if(Utils.isLollipop()){
            overlay.setElevation(Utils.dpToPx(recyclerView.getContext(), 0));
        }

        DragInterface adapter = (DragInterface) recyclerView.getAdapter();
        int end = adapter.getPositionForId(draggingId);
        adapter.onDragEnded(startPos, end);

    }

    private void paintViewToOverlay(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        canvas.drawColor(recyclerView.getResources().getColor(R.color.background_material_light));
        view.draw(canvas);
        overlay.setImageBitmap(bitmap);
        overlay.setTop(0);
    }

    public boolean isInPreviousBounds() {
        float overlayTop = overlay.getTop() + overlay.getTranslationY();
        float overlayBottom = overlay.getBottom() + overlay.getTranslationY();
        return overlayTop < startBounds.bottom && overlayBottom > startBounds.top;
    }
}
