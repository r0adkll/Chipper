package com.r0adkll.chipper.ui.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.r0adkll.chipper.R;

import butterknife.ButterKnife;

/**
 * Created by r0adkll on 11/12/14.
 */
public class IconDrawerItem extends DrawerItem {

    private int text;
    private int icon;

    /**
     * Constructor
     *
     * @param textResId     the text of the drawer item
     * @param iconResId     the icon resource id to display
     */
    public IconDrawerItem(int id, int textResId, int iconResId){
        super(id);
        this.text = textResId;
        this.icon = iconResId;
    }

    /**
     * Called to create this view
     *
     * @param inflater      the layout inflater to inflate a layout from system
     * @param container     the container the layout is going to be placed in
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        Context ctx = inflater.getContext();
        View view = inflater.inflate(R.layout.navdrawer_item, container, false);

        ImageView iconView = ButterKnife.findById(view, R.id.icon);
        TextView titleView = ButterKnife.findById(view, R.id.title);

        // Set the text
        titleView.setText(text);

        // Set the icon, if provided
        iconView.setVisibility(icon > 0 ? View.VISIBLE : View.GONE);
        if(icon > 0)
            iconView.setImageResource(icon);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                ctx.getResources().getColor(R.color.navdrawer_text_color_selected) :
                ctx.getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                ctx.getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                ctx.getResources().getColor(R.color.navdrawer_icon_tint));

        return view;
    }

}
