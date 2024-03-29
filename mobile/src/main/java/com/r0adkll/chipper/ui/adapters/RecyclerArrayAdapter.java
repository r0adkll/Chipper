package com.r0adkll.chipper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.qualifiers.OfflineSwitchPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;


public abstract class RecyclerArrayAdapter<M, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject
    @OfflineSwitchPreference
    BooleanPreference mOfflinePref;

    private OnItemOptionSelectedListener<M> itemOptionSelectedListener;
    private OnItemClickListener<M> itemClickListener;

    protected ArrayList<M> items = new ArrayList<>();
    protected ArrayList<M> filteredItems = new ArrayList<>();
    protected String filter = "";

    private View mEmptyView;

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
     * Set the empty view to be used so that
     * @param emptyView
     */
    public void setEmptyView(View emptyView){
        if(mEmptyView != null){
            unregisterAdapterDataObserver(mEmptyObserver);
        }
        mEmptyView = emptyView;
        registerAdapterDataObserver(mEmptyObserver);
    }

    /**
     * Check if we should show the empty view
     */
    private void checkIfEmpty(){
        if(mEmptyView != null){
            mEmptyView.setVisibility(getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Data change observer
     */
    private RecyclerView.AdapterDataObserver mEmptyObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }
    };

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
        filter = "";
        filter();
        notifyDataSetChanged();
    }

    /**
     * Apply a filter to this adapters subset of content
     */
    private void filter(){
        if((filter != null && !filter.isEmpty()) || (mOfflinePref != null && mOfflinePref.get())){
            if(filter == null) filter = "";

            // Filter out the items
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

        // Notify of filtration
        onFiltered();
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

    /**
     * Override so you can sort the items in the array according
     * to your specification. Do nothing if you choose not to sort, or
     * plan to on your own accord.
     *
     * @param items     the list of items needing sorting
     */
    public abstract void onSort(List<M> items);

    /**
     * Override this method to be notified when the adapter is filtered
     */
    public void onFiltered(){}

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
     * Add a collection of objects to this adapter
     *ß
     * @param collection        the collection of objects to add
     */
    public void addAllRaw(Collection<? extends M> collection) {
        if (collection != null) {
            items.addAll(collection);
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
        items.clear();
        filteredItems.clear();
        filter = null;
        notifyDataSetChanged();
    }

    public void clearRaw(){
        items.clear();
        filteredItems.clear();
        filter = null;
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

    public M remove(int index){
        M item = items.remove(index);
        filter();
        notifyDataSetChanged();
        return item;
    }

    public M removeRaw(int index){
        M item = items.remove(index);
        notifyItemRemoved(index);
        return item;
    }

    public void moveItem(int start, int end){
        int max = Math.max(start, end);
        int min = Math.min(start, end);
        if (min >= 0 && max < items.size()) {
            M item = items.remove(min);
            items.add(max, item);
        }

        // Assume no filtering when you are rearranging items in the adapter
        filteredItems.clear();
        filteredItems.addAll(items);

        notifyItemMoved(min, max);
    }

    public void reconcile(){
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

    public void sort(){
        onSort(items);
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
