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

import butterknife.ButterKnife;

/**
 * Created by r0adkll on 12/16/14.
 */
public class MostPlayedCard extends DashboardCard {
    public static final int ID = 1;

    public static final int LIMIT = 5;

    /**
     * Default Constructor
     *
     * @param ctx the context reference
     */
    public MostPlayedCard(Context ctx) {
        super(ctx);
    }

    @Override
    public CharSequence getTitle() {
        return getContext().getString(R.string.dashboard_mostplayed_header_title);
    }

    @Override
    public View getContentView(View contentView) {
        LinearLayout container;
        if(contentView != null){
            container = (LinearLayout) contentView;
        }else{
            container = new LinearLayout(getContext());
            container.setOrientation(LinearLayout.VERTICAL);
        }

        // Build recent's content
        constructChronicleList(Historian.with(getContext()).getMostPlayed(LIMIT),
                container, getContext().getString(R.string.dashboard_mostplayed_empty_msg));

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
            View view;
            if(i < container.getChildCount()){
                view = container.getChildAt(i);
            }else{
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.layout_most_played_chronicle_item, container, false);
                container.addView(view);
            }

            TextView title = ButterKnife.findById(view, R.id.title);
            TextView playCount = ButterKnife.findById(view, R.id.play_count);
            playCount.setVisibility(View.VISIBLE);
            title.setText(String.format("%s - %s", record.chiptune.artist, record.chiptune.title));
            playCount.setText(String.valueOf(record.play_count));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicPlayer.createPlayback(getContext(), record.chiptune);
                }
            });

        }

        int leftovers = container.getChildCount() - records.size();
        if(leftovers > 0){
            container.removeViews(records.size(), leftovers);
        }

        // check for empty state
        if(records.size() == 0){
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_most_played_chronicle_item, container, false);
            TextView title = ButterKnife.findById(view, R.id.title);
            TextView playCount = ButterKnife.findById(view, R.id.play_count);

            title.setText(emptyMessage);
            playCount.setVisibility(View.GONE);
            container.addView(view);
        }

    }
}
