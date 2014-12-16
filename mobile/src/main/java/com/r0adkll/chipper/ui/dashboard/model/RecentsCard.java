package com.r0adkll.chipper.ui.dashboard.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.data.Historian;
import com.r0adkll.chipper.ui.player.MusicPlayer;

import java.util.List;

/**
 * Created by r0adkll on 12/16/14.
 */
public class RecentsCard extends DashboardCard {
    public static final int ID = 0;

    public static final int LIMIT = 5;

    /**
     * Default Constructor
     *
     * @param ctx the context reference
     */
    public RecentsCard(Context ctx) {
        super(ctx);
    }

    @Override
    public CharSequence getTitle() {
        return getContext().getString(R.string.dashboard_recents_header_title);
    }

    @Override
    public View getContentView(View contentView) {
        LinearLayout container = null;
        if(contentView != null){
            container = (LinearLayout) contentView;
        }else{
            container = new LinearLayout(getContext());
            container.setOrientation(LinearLayout.VERTICAL);
        }

        // Build recent's content
        constructChronicleList(Historian.with(getContext()).getRecentlyPlayed(LIMIT),
                container, getContext().getString(R.string.dashboard_recents_empty_msg));

        // Return the new found content
        return container;
    }

    @Override
    public int getId() {
        return ID;
    }

    /**
     * Build the recent's list ui
     */
    private void constructChronicleList(List<Historian.Chronicle> records, LinearLayout container, String emptyMessage){

        // Iterate through the list of recents and construct their UI
        for(int i=0; i<records.size(); i++){
            final Historian.Chronicle record = records.get(i);

            // Now get the view
            TextView view = null;
            if(i < container.getChildCount()){
                view = (TextView) container.getChildAt(i);
                bindRecord(view, record);
            }else{
                view = (TextView) LayoutInflater.from(getContext())
                        .inflate(R.layout.layout_chronicle_item, container, false);
                bindRecord(view, record);
                container.addView(view);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicPlayer.startPlayback(getContext(),
                            MusicPlayer.createPlayback(getContext(), record.chiptune));
                }
            });

        }

        int leftovers = container.getChildCount() - records.size();
        if(leftovers > 0){
            container.removeViews(records.size(), leftovers);
        }

        // check for empty state
        if(records.size() == 0){
            TextView view = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_chronicle_item, container, false);

            view.setText(emptyMessage);
            container.addView(view);
        }

    }

    private void bindRecord(TextView view, Historian.Chronicle record){
        String text = String.format("%s - %s", record.chiptune.artist, record.chiptune.title);
        view.setText(text);
    }
}
