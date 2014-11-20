package com.r0adkll.chipper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.ChiptuneProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 11/19/14.
 */
public class PlaylistAdapter extends RecyclerArrayAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder> {

    @Inject
    ChiptuneProvider mChiptuneProvider;

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
    public void onBindViewHolder(final PlaylistViewHolder holder, int i) {
        Playlist playlist = getItem(i);

        int size = playlist.getChiptunes(mChiptuneProvider).size();

        holder.title.setText(playlist.name);
        holder.tuneCount.setText(String.format("%d songs", size));

        String description = String.format("Updated %s", mDateFormat.format(new Date(playlist.updated*1000)));
        holder.description.setText(description);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = holder.getPosition();
                onItemClick(v, i);
            }
        });
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.description) TextView description;
        @InjectView(R.id.tune_count) TextView tuneCount;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
