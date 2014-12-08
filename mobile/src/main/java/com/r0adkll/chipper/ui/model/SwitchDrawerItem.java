package com.r0adkll.chipper.ui.model;

import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.prefs.BooleanPreference;

import butterknife.ButterKnife;

/**
 * Created by r0adkll on 11/13/14.
 */
public class SwitchDrawerItem extends DrawerItem implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private int mText;
    private SwitchCompat mSwitch;
    private BooleanPreference mPreference;
    private OnSwitchToggleListener mListener;

    /**
     * Constructor
     */
    public SwitchDrawerItem(int id, int text, BooleanPreference preference) {
        super(id);
        mText = text;
        mPreference = preference;
    }

    /**
     * Constructor
     */
    public SwitchDrawerItem(int id, int text, BooleanPreference preference, OnSwitchToggleListener listener) {
        super(id);
        mText = text;
        mPreference = preference;
        mListener = listener;
    }

    public SwitchCompat getSwitch(){
        return mSwitch;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.navdrawer_item_switch, container, false);

        TextView titleView = ButterKnife.findById(view, R.id.title);
        mSwitch = ButterKnife.findById(view, R.id.item_switch);

        // Apply the preference
        mSwitch.setChecked(mPreference.get());

        // Set the switch checked change listener that updates the set boolean preference
        mSwitch.setOnCheckedChangeListener(this);

        view.setOnClickListener(this);

        // Set the title of the drawer item
        titleView.setText(mText);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPreference.set(isChecked);
        if(mListener != null) mListener.onToggled(isChecked);
    }

    @Override
    public void onClick(View v) {
        mSwitch.setChecked(!mSwitch.isChecked());
    }

    public interface OnSwitchToggleListener{
        public void onToggled(boolean checked);
    }
}
