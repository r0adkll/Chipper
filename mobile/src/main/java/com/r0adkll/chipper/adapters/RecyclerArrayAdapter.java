package com.r0adkll.chipper.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public abstract class RecyclerArrayAdapter<M, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<M> items = new ArrayList<M>();

    public RecyclerArrayAdapter() {
        setHasStableIds(true);
    }

    public void add(M object) {
        items.add(object);
        notifyItemInserted(items.size()-1);
    }

    public void add(int index, M object) {
        items.add(index, object);
        notifyItemInserted(index);
    }

    public void addAll(Collection<? extends M> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyItemRangeInserted(items.size()-collection.size(), collection.size());
        }
    }

    public void addAll(M... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        int N = items.size();
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(M object) {
        int index = items.indexOf(object);
        items.remove(object);
        notifyItemRemoved(index);
    }

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
}
