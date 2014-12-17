package com.r0adkll.chipper.ui.dashboard.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.Historian;
import com.r0adkll.chipper.ui.player.MusicPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by r0adkll on 12/16/14.
 */
public class MostCompletedServerCard extends DashboardCard implements Callback<List<Historian.Chronicle>>{
    public static final int ID = 1;

    public static final int LIMIT = 5;

    private ChipperService mService;
    private ChiptuneProvider mProvider;

    private LinearLayout mContainer;
    private ProgressBar mLoading;

    /**
     * Default Constructor
     *
     * @param ctx the context reference
     */
    public MostCompletedServerCard(Context ctx, ChipperService service, ChiptuneProvider provider) {
        super(ctx);
        mService = service;
        mProvider = provider;
    }

    @Override
    public CharSequence getTitle() {
        return getContext().getString(R.string.dashboard_mostcompleted_server_header_title);
    }

    @Override
    public View getContentView(View contentView) {
        RelativeLayout content;
        if(contentView != null){
            content = (RelativeLayout) contentView;
            mContainer = ButterKnife.findById(content, R.id.container);
            mLoading = ButterKnife.findById(content, R.id.loading);
        }else{
            content = new RelativeLayout(getContext());

            mLoading = new ProgressBar(getContext());
            mLoading.setIndeterminate(true);
            mLoading.setId(R.id.loading);
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            content.addView(mLoading, params);

            mContainer = new LinearLayout(getContext());
            mContainer.setOrientation(LinearLayout.VERTICAL);
            mContainer.setId(R.id.container);
            content.addView(mContainer);
        }

        // Request server's most recent
        mLoading.setVisibility(View.VISIBLE);
        mService.getMostCompleted(LIMIT, this);

        // Return the new found content
        return content;
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
            playCount.setText(String.valueOf(record.completed_count));

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
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_most_played_chronicle_item, container, false);
            TextView title = ButterKnife.findById(view, R.id.title);
            TextView playCount = ButterKnife.findById(view, R.id.play_count);

            title.setText(emptyMessage);
            playCount.setVisibility(View.GONE);
            container.addView(view);
        }

    }

    @Override
    public void success(List<Historian.Chronicle> chronicles, Response response) {
        // Find all the chronicles
        for(Historian.Chronicle chronicle: chronicles){
            chronicle.chiptune = mProvider.getChiptune(chronicle.chiptune_id);
        }

        mLoading.setVisibility(View.GONE);
        constructChronicleList(chronicles, mContainer, getContext().getString(R.string.dashboard_mostplayed_empty_msg));
    }

    @Override
    public void failure(RetrofitError error) {
        mLoading.setVisibility(View.GONE);
        constructChronicleList(new ArrayList<Historian.Chronicle>(), mContainer, getContext().getString(R.string.dashboard_mostplayed_empty_msg));
    }
}
