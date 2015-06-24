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
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.FeaturedChiptuneReference;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.qualifiers.OfflineSwitchPreference;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.adapters
 * Created by drew.heavner on 11/20/14.
 */
public class FeaturedChiptuneAdapter extends RecyclerArrayAdapter<FeaturedChiptuneReference, FeaturedChiptuneAdapter.PlaylistChiptuneViewHolder> {

    @Inject
    ChiptuneProvider mChiptuneProvider;

    @Inject
    PlaylistManager mPlaylistManager;

    @Inject
    VoteManager mVoteManager;

    @Inject
    CashMachine mAtm;

    @Inject @OfflineSwitchPreference
    BooleanPreference mOfflinePref;

    /**
     * Constructor
     */
    @Inject
    public FeaturedChiptuneAdapter(){
        super();
    }

    @Override
    public PlaylistChiptuneViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_featured_chiptune_item, viewGroup, false);

        return new PlaylistChiptuneViewHolder(view);
    }

    @Override
    public boolean onQuery(FeaturedChiptuneReference item, String query) {
        Chiptune chiptune = mChiptuneProvider.getChiptune(item.chiptune_id);
        if(mOfflinePref.get()) {
            if(!mAtm.isOffline(chiptune)) return false;
        }

        return chiptune.artist.toLowerCase().contains(query.toLowerCase()) ||
                chiptune.title.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public void onSort(List<FeaturedChiptuneReference> items) {
        Collections.sort(items, new Comparator<FeaturedChiptuneReference>() {
            @Override
            public int compare(FeaturedChiptuneReference lhs, FeaturedChiptuneReference rhs) {
                int lhsSO = lhs.sort_order;
                int rhsSO = rhs.sort_order;
                return lhsSO < rhsSO ? -1 : (lhsSO == rhsSO ? 0 : 1);
            }
        });
    }

    @Override
    public void onBindViewHolder(final PlaylistChiptuneViewHolder holder, int i) {
        FeaturedChiptuneReference chiptuneReference = getItem(i);
        Chiptune chiptune = mChiptuneProvider.getChiptune(chiptuneReference.chiptune_id);

        // Bind data to view
        holder.title.setText(chiptune.title);
        holder.artist.setText(chiptune.artist);
        holder.length.setText(chiptune.getFormattedLength());

        holder.optFav.setOnClickListener(new OptionClickListener(i));
        holder.optUpvote.setOnClickListener(new OptionClickListener(i));
        holder.optDownvote.setOnClickListener(new OptionClickListener(i));
        holder.optAdd.setOnClickListener(new OptionClickListener(i));
        holder.optOffline.setOnClickListener(new OptionClickListener(i));

        // Color the Vote Options
        int userVoteValue = mVoteManager.getUserVoteValue(chiptune.id);
        int accentColor = holder.itemView.getContext().getResources().getColor(R.color.accentColor);
        if(userVoteValue == 1){
            holder.optUpvote.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            holder.optDownvote.clearColorFilter();
        }else if(userVoteValue == -1){
            holder.optUpvote.clearColorFilter();
            holder.optDownvote.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
        }else{
            holder.optUpvote.clearColorFilter();
            holder.optDownvote.clearColorFilter();
        }

        // Color the favorites option
        if(mPlaylistManager.isFavorited(chiptune)){
            holder.optFav.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
        }else{
            holder.optFav.clearColorFilter();
        }

        // Color the Offline option
        if(mAtm.isOffline(chiptune)){
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


    /**
     * The viewholder for this adapter
     */
    public static class PlaylistChiptuneViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title)     TextView title;
        @InjectView(R.id.artist)    TextView artist;
        @InjectView(R.id.length)    TextView length;
        @InjectView(R.id.opt_favorite)  ImageView optFav;
        @InjectView(R.id.opt_upvote)    ImageView optUpvote;
        @InjectView(R.id.opt_downvote)  ImageView optDownvote;
        @InjectView(R.id.opt_add)       ImageView optAdd;
        @InjectView(R.id.opt_offline)   ImageView optOffline;

        public PlaylistChiptuneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
