package com.r0adkll.chipper.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftinc.fontloader.FontLoader;
import com.ftinc.fontloader.Types;
import com.r0adkll.chipper.R;
import com.r0adkll.deadskunk.utils.Utils;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.widget
 * Created by drew.heavner on 11/20/14.
 */
public class EmptyView extends RelativeLayout {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private ImageView mIcon;
    private TextView mMessage;

    private int mEmptyIcon = R.drawable.ic_launcher;
    private int mAccentColor;
    private String mEmptyMessage = "You currently don't have any items";

    /***********************************************************************************************
     *
     * Constructors
     *
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttributes(context, attrs, defStyleAttr);
        init();
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(context, attrs, defStyleAttr);
        init();
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs, 0);
        init();
    }

    public EmptyView(Context context) {
        super(context);
        init();
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Parse XML attributes
     *
     * @param attrs     the attributes to parse
     */
    private void parseAttributes(Context context, AttributeSet attrs, int defStyle){
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.EmptyView, defStyle, 0);
        if (a == null) {
            mAccentColor = context.getResources().getColor(R.color.empty_layout_default);
            return;
        }

        // Parse attributes
        mEmptyMessage = a.getString(R.styleable.EmptyView_emptyMessage);
        mAccentColor = a.getColor(R.styleable.EmptyView_accentColor, context.getResources().getColor(R.color.empty_layout_default));
        mEmptyIcon = a.getResourceId(R.styleable.EmptyView_emptyIcon, R.drawable.ic_launcher);
    }

    /**
     * Initialize the Empty Layout
     */
    private void init(){

        // Create the Empty Layout
        LinearLayout container = new LinearLayout(getContext());
        mIcon = new ImageView(getContext());
        mMessage = new TextView(getContext());

        // Setup the layout
        RelativeLayout.LayoutParams containerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.setGravity(Gravity.CENTER);
        container.setOrientation(LinearLayout.VERTICAL);
        containerParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        // Setup the Icon
        int size = (int) Utils.dpToPx(getContext(), 64 );
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
        int padding = getResources().getDimensionPixelSize(R.dimen.half_padding);
        mIcon.setPadding(0, 0, 0, padding);
        mIcon.setColorFilter(mAccentColor, PorterDuff.Mode.SRC_IN);
        mIcon.setImageResource(mEmptyIcon);

        // Setup the message
        LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mMessage.setTextColor(mAccentColor);
        mMessage.setGravity(Gravity.CENTER);
        mMessage.setText(mEmptyMessage);
        FontLoader.applyTypeface(mMessage, Types.ROBOTO_MEDIUM);

        // Add to the layout
        container.addView(mIcon, iconParams);
        container.addView(mMessage, msgParams);

        // Add to view
        addView(container, containerParams);
    }

    public void setAccentColor(int colorResId){
        mAccentColor = getResources().getColor(colorResId);
        mIcon.setColorFilter(mAccentColor, PorterDuff.Mode.SRC_IN);
        mMessage.setTextColor(mAccentColor);
    }

    public void setIcon(int resId){
        mIcon.setImageResource(resId);
    }

    public void setEmptyMessage(CharSequence message){
        mMessage.setText(message);
    }

}
