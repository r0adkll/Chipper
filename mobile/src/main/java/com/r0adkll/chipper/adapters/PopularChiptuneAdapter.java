package com.r0adkll.chipper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.core.api.model.Chiptune;

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

    private OnItemClickListener mClickListener;

    /**
     * Constructor
     */
    public PopularChiptuneAdapter(){
        super();
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
    public void setVoteData(){
        // Update the vote data reference


        // Notify of changes
        notifyDataSetChanged();
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
                .inflate(0, viewGroup, false);

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

        // Bind data to view holder
        holder.title.setText(tune.title);

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
     * View Holder
     *
     */

    /**
     * View Holder for this adapter
     */
    public static class PopularViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title)     TextView title;
//        @InjectView(R.id.artist)    TextView artist;
//        @InjectView(R.id.vote)      TextView vote;
//        @InjectView(R.id.length)    TextView length;

        public PopularViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static interface OnItemClickListener{
        public void onItemClick(View v, Chiptune item, int position);
    }

}
