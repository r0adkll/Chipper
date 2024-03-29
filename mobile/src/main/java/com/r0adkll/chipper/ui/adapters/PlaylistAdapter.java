package com.r0adkll.chipper.ui.adapters;

import android.app.Application;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.qualifiers.OfflineSwitchPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 11/19/14.
 */
public class PlaylistAdapter extends RecyclerArrayAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder> {

    @Inject ChiptuneProvider mChiptuneProvider;
    @Inject CashMachine mAtm;
    @Inject Application mApp;
    @Inject @OfflineSwitchPreference
    BooleanPreference mOfflinePref;

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("M/d/yy 'at' HH:mm a");

    /**
     * Constructor
     */
    @Inject
    public PlaylistAdapter(){
        super();
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_playlist_item, viewGroup, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public boolean onQuery(Playlist item, String query) {
        if(mOfflinePref.get()){
            if(!item.isPartiallyOffline(mAtm)) return false;
        }
        return item.name.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public void onSort(List<Playlist> items) {
        // Do nothing
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, int i) {
        Playlist playlist = getItem(i);

        int size = playlist.getChiptunes(mChiptuneProvider).size();

        holder.title.setText(playlist.name);
        holder.tuneCount.setText(String.format("%d songs", size));

        String description = String.format("Updated %s", mDateFormat.format(new Date(playlist.updated*1000)));
        holder.description.setText(description);

        if(playlist.isOffline(mAtm)){
            holder.offline.setVisibility(View.VISIBLE);
            holder.offline.setColorFilter(mApp.getResources().getColor(R.color.primaryDark), PorterDuff.Mode.SRC_IN);
        }else{
            holder.offline.setVisibility(View.GONE);
        }

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int i = holder.getPosition();
//                onItemClick(v, i);
//            }
//        });
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.description) TextView description;
        @InjectView(R.id.tune_count) TextView tuneCount;
        @InjectView(R.id.playlist_offline) ImageView offline;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
