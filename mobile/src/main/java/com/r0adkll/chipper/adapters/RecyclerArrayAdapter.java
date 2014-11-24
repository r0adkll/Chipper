package com.r0adkll.chipper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public abstract class RecyclerArrayAdapter<M, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private OnItemOptionSelectedListener<M> itemOptionSelectedListener;
    private OnItemClickListener<M> itemClickListener;
    private ArrayList<M> items = new ArrayList<M>();

    /**
     * Default Constructor
     */
    public RecyclerArrayAdapter() {
        setHasStableIds(true);
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Set the item click listener for this adapter
     */
    public void setOnItemClickListener(OnItemClickListener<M> itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemOptionSelectedListener(OnItemOptionSelectedListener<M> itemOptionSelectedListener){
        this.itemOptionSelectedListener = itemOptionSelectedListener;
    }

    /**
     * Call this to trigger the user set item click listener
     *
     * @param view          the view that was clicked
     * @param position      the position that was clicked
     */
    protected void onItemClick(View view, int position){
        if(itemClickListener != null) itemClickListener.onItemClick(view, getItem(position), position);
    }

    protected void onItemOptionSelected(View view, int position){
        if(itemOptionSelectedListener != null) itemOptionSelectedListener.onSelected(view, getItem(position));
    }

    /***********************************************************************************************
     *
     * Array Methods
     *
     */

    /**
     * Add a single object to this adapter
     * @param object    the object to add
     */
    public void add(M object) {
        items.add(object);
        notifyDataSetChanged();
    }

    /**
     * Add a single object at the given index
     *
     * @param index     the position to add the object at
     * @param object    the object to add
     */
    public void add(int index, M object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    /**
     * Add a collection of objects to this adapter
     *
     * @param collection        the collection of objects to add
     */
    public void addAll(Collection<? extends M> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    /**
     * Add a list of objects to this adapter
     *
     * @param items     the list of items to add
     */
    public void addAll(M... items) {
        addAll(Arrays.asList(items));
    }

    /**
     * Clear this adapter of all items
     */
    public void clear() {
        int N = items.size();
        items.clear();
        notifyDataSetChanged();
    }

    /**
     * Remove a specific object from this adapter
     *
     * @param object        the object to remove
     */
    public void remove(M object) {
        int index = items.indexOf(object);
        items.remove(object);
        notifyDataSetChanged();
    }

    public void remove(int index){
        items.remove(index);
    }

    /**
     * Sort the items in this adapter by a given
     * {@link java.util.Comparator}
     *
     * @param comparator        the comparator to sort with
     */
    public void sort(Comparator<M> comparator){
        Collections.sort(items, comparator);
        notifyDataSetChanged();
    }

    /**
     * Get an item from this adapter at a specific index
     *
     * @param position      the position of the item to retrieve
     * @return              the item at that position, or null
     */
    public M getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static interface OnItemOptionSelectedListener<M>{
        public void onSelected(View view, M item);
    }

}
