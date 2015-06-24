package com.r0adkll.chipper.ui.screens.dashboard.model;

import android.content.Context;
import android.view.View;

/**
 * Created by r0adkll on 12/16/14.
 */
public abstract class DashboardCard {

    /* Context Reference */
    private Context mContext;

    /**
     * Default Constructor
     * @param ctx       the context reference
     */
    public DashboardCard(Context ctx){
        mContext = ctx;
    }

    /**
     * Get the underlying context reference
     * @return      the context reference
     */
    protected Context getContext(){
        return mContext;
    }

    /**
     * Get the title of this card
     *
     * @return      the cards title
     */
    public abstract CharSequence getTitle();

    /**
     * Get the content of this card to be placed by the adapter
     *
     * @return      this card's content view
     */
    public abstract View getContentView(View contentView);

    /**
     * Get the unique ID for this card type so that the adapter can distinguish it's
     * old content data for recycling
     *
     * @return      a unique id to this card type
     */
    public abstract int getId();

}
