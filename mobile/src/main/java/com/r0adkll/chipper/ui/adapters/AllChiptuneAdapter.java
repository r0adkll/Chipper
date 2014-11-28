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
import com.r0adkll.chipper.data.VoteManager;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.adapters
 * Created by drew.heavner on 11/13/14.
 */
public class AllChiptuneAdapter extends RecyclerArrayAdapter<Chiptune, AllChiptuneAdapter.ChiptuneViewHolder>
        implements StickyRecyclerHeadersAdapter<AllChiptuneAdapter.HeaderViewHolder> {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private VoteManager mVoteManager;
    private List<List<Chiptune>> mHeaders = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();

    /**
     * Constructor
     *
     */
    public AllChiptuneAdapter(VoteManager voteManager){
        super();
        registerAdapterDataObserver(mChiptunesObserver);
        mVoteManager = voteManager;

    }

    /***********************************************************************************************
     *
     * Adapter Methods
     *
     */

    @Override
    public ChiptuneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_all_chiptune_item, parent, false);

        return new ChiptuneViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChiptuneViewHolder holder, int position) {
        Chiptune data = getItem(position);

        holder.title.setText(data.title);
        holder.description.setText(data.getFormattedLength());

        holder.optFav.setOnClickListener(new OptionClickListener(position));
        holder.optUpvote.setOnClickListener(new OptionClickListener(position));
        holder.optDownvote.setOnClickListener(new OptionClickListener(position));
        holder.optAdd.setOnClickListener(new OptionClickListener(position));
        holder.optOffline.setOnClickListener(new OptionClickListener(position));

        int voteValue = mVoteManager.getUserVoteValue(data.id);
        int accentColor = holder.itemView.getContext().getResources().getColor(R.color.accentColor);
        if(voteValue == 1){
            holder.optUpvote.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            holder.optDownvote.clearColorFilter();
        }else if(voteValue == -1){
            holder.optUpvote.clearColorFilter();
            holder.optDownvote.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
        }else{
            holder.optUpvote.clearColorFilter();
            holder.optDownvote.clearColorFilter();
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
     * Sticky Headers Adapter
     *
     */

    @Override
    public long getHeaderId(int i) {
        Chiptune data = getItem(i);

        for(List<Chiptune> items: mHeaders){
            if(items.contains(data)){
                return mHeaders.indexOf(items);
            }
        }

        return 0;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_all_chiptune_header, viewGroup, false);

        return new HeaderViewHolder(itemView);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {

        long id = getHeaderId(position);
        String artist = mTitles.get((int)id);
        holder.title.setText(artist);

    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Build the section headers for use in creating the headers
     */
    private void buildSectionHeaders(){
        // Update Artist maps
        HashMap<String, List<Chiptune>> currMap = new HashMap<String, List<Chiptune>>();

        // Loop through tuneRefs
        for(int i=0; i<getItemCount(); i++){
            Chiptune tune = getItem(i);
            String artist = tune.artist.toUpperCase();
            List<Chiptune> tracks = currMap.get(artist);
            if(tracks != null){
                tracks.add(tune);
                currMap.put(artist, tracks);
            }else{
                tracks = new ArrayList<>();
                tracks.add(tune);
                currMap.put(artist, tracks);
            }
        }

        // Update maps
        mHeaders.clear();
        mHeaders.addAll(currMap.values());
        mTitles.clear();
        mTitles.addAll(currMap.keySet());
    }

    private RecyclerView.AdapterDataObserver mChiptunesObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();

            // Rebuild Header maps
            buildSectionHeaders();
        }
    };


    /***********************************************************************************************
     *
     * ViewHolders
     *
     */

    /**
     * The ViewHolder for this adapter
     */
    public static class ChiptuneViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.title)         TextView title;
        @InjectView(R.id.description)   TextView description;
        @InjectView(R.id.opt_favorite)  ImageView optFav;
        @InjectView(R.id.opt_upvote)    ImageView optUpvote;
        @InjectView(R.id.opt_downvote)  ImageView optDownvote;
        @InjectView(R.id.opt_add)       ImageView optAdd;
        @InjectView(R.id.opt_offline)   ImageView optOffline;

        public ChiptuneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    /**
     * The header ViewHolder for this adapter
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title)     TextView title;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}