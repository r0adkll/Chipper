package com.r0adkll.chipper.tv.ui.model;

import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.r0adkll.chipper.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 12/8/14.
 */
public class PreferencePresenter extends Presenter {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_preference_item, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ViewHolder holder = (ViewHolder) viewHolder;
        PreferenceItem prefItem = (PreferenceItem) item;
        holder.apply(prefItem);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        // DO Nothing
    }


    static class ViewHolder extends Presenter.ViewHolder {

        @InjectView(R.id.icon)  ImageView icon;
        @InjectView(R.id.text)  TextView text;

        /**
         * Constructor
         */
        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        /**
         * Apply the preference item to this view
         * @param item
         */
        public void apply(PreferenceItem item){
            text.setText(item.text);
            icon.setImageResource(item.icon);

            icon.setColorFilter(view.getResources().getColor(R.color.navdrawer_icon_tint));
        }

    }

    /**
     * Preference Item ModelObejct
     */
    public static class PreferenceItem{
        public String text;
        public int icon;

        public PreferenceItem(String text, int icon){
            this.text = text;
            this.icon = icon;
        }
    }

}
