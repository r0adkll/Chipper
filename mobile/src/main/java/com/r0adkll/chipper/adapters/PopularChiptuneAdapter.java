package com.r0adkll.chipper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 11/15/14.
 */
public class PopularChiptuneAdapter extends RecyclerArrayAdapter<Chiptune, PopularChiptuneAdapter.PopularViewHolder> {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private Map<String, Integer> voteData;

    private OnItemClickListener mClickListener;

    /**
     * Constructor
     */
    public PopularChiptuneAdapter(){
        super();
        voteData = new HashMap<>();
    }

    /**
     * Set the item click listener
     * @param listener      the click listener
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        mClickListener = listener;
    }

    /**
     * Update the adapters reference to the new vote data and notify of a data set change
     */
    public void setVoteData(Map<String, Integer> voteData){
        // Update the vote data reference
        voteData.putAll(voteData);

        // Sort Votedata by votes
        sort(new PopularComparator(voteData));
    }

    /***********************************************************************************************
     *
     *  Adapter Methods
     *
     */

    @Override
    public PopularViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflate View
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_popular_item, viewGroup, false);

        // Create ViewHolder
        PopularViewHolder holder = new PopularViewHolder(view);

        // Return the holder
        return holder;
    }

    @Override
    public void onBindViewHolder(final PopularViewHolder holder, int i) {
        Chiptune tune = getItem(i);

        // Compute time
        String time = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tune.length),
                TimeUnit.MILLISECONDS.toSeconds(tune.length) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tune.length)));
        holder.length.setText(time);

        // Get vote data value
        int voteValue = getVoteValue(voteData, tune.id);
        holder.vote.setText(String.valueOf(voteValue));

        // Bind data to view holder
        holder.title.setText(tune.title);
        holder.artist.setText(tune.artist);

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = holder.getPosition();
                if(mClickListener != null) mClickListener.onItemClick(v, getItem(i), i);
            }
        });

    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Safely get a vote value from the vote datamap
     *
     * @param data      the vote data
     * @param key       the key of the vote value to get
     * @return          the vote value, or 0 if not found
     */
    public static int getVoteValue(Map<String, Integer> data, String key){
        Integer value = data.get(key);
        if(value == null){
            return 0;
        }

        return value;
    }

    /***********************************************************************************************
     *
     * View Holder
     *
     */

    /**
     * View Holder for this adapter
     */
    public static class PopularViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title)     TextView title;
        @InjectView(R.id.artist)    TextView artist;
        @InjectView(R.id.vote)      TextView vote;
        @InjectView(R.id.length)    TextView length;

        public PopularViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static class PopularComparator implements Comparator<Chiptune>{
        private Map<String, Integer> voteData;

        public PopularComparator(Map<String, Integer> voteData){
            this.voteData = voteData;
        }

        @Override
        public int compare(Chiptune lhs, Chiptune rhs) {
            int lhsVoteValue = getVoteValue(voteData, lhs.id);
            int rhsVoteValue = getVoteValue(voteData, rhs.id);

            // First compare the vote values
            if(lhsVoteValue > rhsVoteValue){
                return 1;
            }else if(lhsVoteValue < rhsVoteValue){
                return -1;
            }else if(lhsVoteValue == rhsVoteValue){
                int c1 = lhs.artist.compareTo(rhs.artist);
                if (c1 == 0) {
                    int c2 = lhs.title.compareTo(rhs.title);
                    return c2;
                }
                return c1;
            }

            return 0;
        }
    }

    public static interface OnItemClickListener{
        public void onItemClick(View v, Chiptune item, int position);
    }

}
