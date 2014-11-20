package com.r0adkll.chipper.adapters;

import android.view.View;

public interface OnItemClickListener<T>{
    public void onItemClick(View v, T item, int position);
}