package com.r0adkll.chipper.adapters;

import android.support.v7.widget.RecyclerView;

import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public abstract class RecyclerArrayAdapter<M, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<M> items = new ArrayList<M>();

    public RecyclerArrayAdapter() {
        setHasStableIds(true);
    }

    public void add(M object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, M object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends M> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
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
        notifyDataSetChanged();
    }

    public void sort(Comparator<M> comparator){
        Collections.sort(items, comparator);
        notifyDataSetChanged();
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
