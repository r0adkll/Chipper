package com.r0adkll.chipper.ui.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.utils.UIUtils;

/**
 * Created by r0adkll on 11/13/14.
 */
public class SeperatorDrawerItem extends DrawerItem{

    /**
     * Blank Constructor
     */
    public SeperatorDrawerItem(){
        super(-2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.navdrawer_separator, container, false);
        UIUtils.setAccessibilityIgnore(view);
        return view;
    }
}
