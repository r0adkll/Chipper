package com.r0adkll.chipper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import hugo.weaving.DebugLog;


public abstract class RecyclerArrayAdapter<M, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private OnItemOptionSelectedListener<M> itemOptionSelectedListener;
    private OnItemClickListener<M> itemClickListener;
    protected ArrayList<M> items = new ArrayList<>();
    protected ArrayList<M> filteredItems = new ArrayList<>();
    protected String filter;

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
     * Query Methods
     *
     */

    /**
     * Apply a query to this adapter
     *
     * @param query     query
     */
    public void query(String query){
        filter = query;

        // Filter results
        filter();

        // Force an application of the query
        notifyDataSetChanged();
    }

    /**
     * Clear out the current query
     */
    public void clearQuery(){
        filter = null;
        filter();
        notifyDataSetChanged();
    }

    /**
     * Apply a filter to this adapters subset of content
     */
    private void filter(){
        if(filter != null && !filter.isEmpty()){
            filteredItems.clear();
            for(M item: items){
                if(onQuery(item, filter)){
                    filteredItems.add(item);
                }
            }
        }else{
            filteredItems.clear();
            filteredItems.addAll(items);
        }
    }

    /**
     * Override this method to apply filtering to your content
     * so you can supply queries to the adapter to filter your content out
     * for search
     *
     * @param item      the item to filter check
     * @param query     the query to check with
     * @return          true if the item matches the query in any way
     */
    public abstract boolean onQuery(M item, String query);

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
        filter();
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
        filter();
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
            filter();
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
        filteredItems.clear();
        filter = null;
        notifyDataSetChanged();
    }

    /**
     * Remove a specific object from this adapter
     *
     * @param object        the object to remove
     */
    public void remove(M object) {
        items.remove(object);
        filter();
        notifyDataSetChanged();
    }

    public void remove(int index){
        items.remove(index);
        filter();
        notifyDataSetChanged();
    }

    /**
     * Sort the items in this adapter by a given
     * {@link java.util.Comparator}
     *
     * @param comparator        the comparator to sort with
     */
    public void sort(Comparator<M> comparator){
        Collections.sort(items, comparator);
        filter();
        notifyDataSetChanged();
    }

    /**
     * Get an item from this adapter at a specific index
     *
     * @param position      the position of the item to retrieve
     * @return              the item at that position, or null
     */
    public M getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public static interface OnItemOptionSelectedListener<M>{
        public void onSelected(View view, M item);
    }

}
