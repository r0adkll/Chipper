package com.r0adkll.chipper.ui.adapters;

/**
 * Created by r0adkll on 12/19/14.
 */
public interface DragInterface {

    public void onDragStarted(int i);

    public void onDragEnded(int start, int end);

    public int getPositionForId(long id);

    public void moveItem(int start, int end);

}
