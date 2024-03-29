package com.r0adkll.chipper.ui.adapters;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.utils.ChiptuneComparator;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.qualifiers.OfflineSwitchPreference;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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

    @Inject PlaylistManager mPlaylistManager;
    @Inject VoteManager mVoteManager;
    @Inject CashMachine mCache;
    @Inject @OfflineSwitchPreference
    BooleanPreference mOfflinePref;

    private Map<String, Integer> voteData = new HashMap<>();

    /**
     * Constructor
     */
    @Inject
    public PopularChiptuneAdapter(){
        super();
    }

    /**
     * Update the adapters reference to the new vote data and notify of a data set change
     */
    public void setVoteData(Map<String, Integer> voteData){
        // Update the vote data reference
        this.voteData.putAll(voteData);

        // Sort Votedata by votes
        sort(new PopularComparator(voteData));
    }

    @Override
    public boolean onQuery(Chiptune item, String query) {
        if(mOfflinePref.get()){
            if(!mCache.isOffline(item)) return false;
        }
        return item.artist.toLowerCase().contains(query.toLowerCase()) ||
                item.title.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public void onSort(List<Chiptune> items) {
        if(voteData != null) {
            Collections.sort(items, new PopularComparator(voteData));
        }else{
            Collections.sort(items, new ChiptuneComparator());
        }
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
        holder.length.setText(tune.getFormattedLength());

        // Get vote data value
        int voteValue = getVoteValue(voteData, tune.id);
        holder.vote.setText(String.valueOf(voteValue));

        // Bind data to view holder
        holder.title.setText(tune.title);
        holder.artist.setText(tune.artist);

        holder.optFav.setOnClickListener(new OptionClickListener(i));
        holder.optUpvote.setOnClickListener(new OptionClickListener(i));
        holder.optDownvote.setOnClickListener(new OptionClickListener(i));
        holder.optAdd.setOnClickListener(new OptionClickListener(i));
        holder.optOffline.setOnClickListener(new OptionClickListener(i));

        // Color the Vote Options
        int userVoteValue = mVoteManager.getUserVoteValue(tune.id);
        int accentColor = holder.itemView.getContext().getResources().getColor(R.color.accentColor);
        int primary = holder.itemView.getContext().getResources().getColor(R.color.primary);
        int textColor = holder.itemView.getContext().getResources().getColor(R.color.body_text_secondary_light);
        if(userVoteValue == 1){
            holder.optUpvote.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            holder.optDownvote.clearColorFilter();
            holder.vote.setTextColor(primary);
        }else if(userVoteValue == -1){
            holder.optUpvote.clearColorFilter();
            holder.optDownvote.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            holder.vote.setTextColor(primary);
        }else{
            holder.optUpvote.clearColorFilter();
            holder.optDownvote.clearColorFilter();
            holder.vote.setTextColor(textColor);
        }

        // Color the favorites option
        if(mPlaylistManager.isFavorited(tune)){
            holder.optFav.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
        }else{
            holder.optFav.clearColorFilter();
        }

        // Color the Offline option
        if(mCache.isOffline(tune)){
            holder.optOffline.setImageResource(R.drawable.ic_action_cloud_done);
            holder.optOffline.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
        }else{
            holder.optOffline.setImageResource(R.drawable.ic_action_cloud_download);
            holder.optOffline.clearColorFilter();
        }

    }

    private class OptionClickListener implements View.OnClickListener {

        private int mPosition = 0;

        public OptionClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            onItemOptionSelected(v, mPosition);
        }
    };

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
        @InjectView(R.id.opt_favorite)  ImageView optFav;
        @InjectView(R.id.opt_upvote)    ImageView optUpvote;
        @InjectView(R.id.opt_downvote)  ImageView optDownvote;
        @InjectView(R.id.opt_add)       ImageView optAdd;
        @InjectView(R.id.opt_offline)   ImageView optOffline;

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
            int result = intCompare(lhsVoteValue, rhsVoteValue);
            if(result == 0){
                int c1 = lhs.artist.compareTo(rhs.artist);
                if (c1 == 0) {
                    int c2 = lhs.title.compareTo(rhs.title);
                    return c2;
                }
                return c1;
            }

            return result;
        }
    }

    public static int intCompare(int lhs, int rhs){
        return lhs < rhs ? 1 : (lhs == rhs ? 0 : -1);
    }

}
