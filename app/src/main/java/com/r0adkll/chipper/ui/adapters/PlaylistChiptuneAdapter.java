package com.r0adkll.chipper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.qualifiers.FlexSpaceHeader;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.qualifiers.OfflineSwitchPreference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.adapters
 * Created by drew.heavner on 11/20/14.
 */
public class PlaylistChiptuneAdapter extends RecyclerArrayAdapter<ChiptuneReference, PlaylistChiptuneAdapter.PlaylistChiptuneViewHolder>
        implements DragInterface{

    @Inject
    ChiptuneProvider mChiptuneProvider;

    @Inject
    CashMachine mAtm;

    @Inject @OfflineSwitchPreference
    BooleanPreference mOfflinePref;

    private OnMoveItemListener mMoveItemListener;

    /**
     * Constructor
     */
    @Inject
    public PlaylistChiptuneAdapter(){
        super();
    }

    public void setOnMoveItemListener(OnMoveItemListener listener){
        mMoveItemListener = listener;
    }

    @Override
    public boolean onQuery(ChiptuneReference item, String query) {
        Chiptune chiptune = mChiptuneProvider.getChiptune(item.chiptune_id);
        if(mOfflinePref.get()) {
            if(!mAtm.isOffline(chiptune)) return false;
        }

        return chiptune.artist.toLowerCase().contains(query.toLowerCase()) ||
                chiptune.title.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public void onSort(List<ChiptuneReference> items) {
        Collections.sort(items, new Comparator<ChiptuneReference>() {
            @Override
            public int compare(ChiptuneReference lhs, ChiptuneReference rhs) {
                int lhsSO = lhs.sort_order;
                int rhsSO = rhs.sort_order;
                return lhsSO < rhsSO ? -1 : (lhsSO == rhsSO ? 0 : 1);
            }
        });
    }

    @Override
    public PlaylistChiptuneViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_playlist_chiptune_item, viewGroup, false);

        return new PlaylistChiptuneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlaylistChiptuneViewHolder holder, final int i) {
        ChiptuneReference chiptuneReference = getItem(i);
        Chiptune chiptune = mChiptuneProvider.getChiptune(chiptuneReference.chiptune_id);

        // Bind data to view
        holder.title.setText(chiptune.title);
        holder.artist.setText(chiptune.artist);
        holder.length.setText(chiptune.getFormattedLength());

        // Do something with the handle here

        // Set Item Click Listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, i);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getPositionForId(long id) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDragStarted(int i) {

    }

    @DebugLog
    @Override
    public void onDragEnded(int start, int end) {
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < items.size(); i++) {
                ChiptuneReference ref = items.get(i);
                ref.sort_order = i;
                ref.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally{
            ActiveAndroid.endTransaction();
        }

        if(mMoveItemListener != null) mMoveItemListener.onItemMove(start, end);
    }

    /**
     * The viewholder for this adapter
     */
    public static class PlaylistChiptuneViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.handle)    ImageView handle;
        @InjectView(R.id.title)     TextView title;
        @InjectView(R.id.artist)    TextView artist;
        @InjectView(R.id.length)    TextView length;

        public PlaylistChiptuneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static interface OnMoveItemListener{
        public void onItemMove(int start, int end);
    }

}
