package com.r0adkll.chipper.ui.adapters;

import android.app.Application;
import android.content.Context;
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
import com.r0adkll.chipper.data.ChiptuneProvider;

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
public class QueueChiptuneAdapter extends RecyclerArrayAdapter<Chiptune, QueueChiptuneAdapter.PlaylistChiptuneViewHolder>
        implements DragInterface{

    private Context ctx;
    private OnMoveItemListener mMoveItemListener;

    /**
     * Constructor
     */
    @Inject
    public QueueChiptuneAdapter(Application app){
        super();
        ctx = app;
    }

    public void setOnMoveItemListener(OnMoveItemListener listener){
        mMoveItemListener = listener;
    }


    @Override
    public PlaylistChiptuneViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_queue_chiptune_item, viewGroup, false);

        return new PlaylistChiptuneViewHolder(view);
    }

    @Override
    public boolean onQuery(Chiptune item, String query) {
        return true;
    }

    @Override
    public void onSort(List<Chiptune> items) {}

    @Override
    public void onBindViewHolder(final PlaylistChiptuneViewHolder holder, int i) {
        Chiptune chiptune = getItem(i);

        if(i == 0){
            holder.blanket.setVisibility(View.VISIBLE);
        }else{
            holder.blanket.setVisibility(View.GONE);
        }

        // Bind data to view
        holder.title.setText(chiptune.title);
        holder.artist.setText(chiptune.artist);
        holder.length.setText(chiptune.getFormattedLength());

        // Do something with the handle here

        // Set Item Click Listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = holder.getPosition();
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
        @InjectView(R.id.blanket)   View blanket;

        public PlaylistChiptuneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static interface OnMoveItemListener{
        public void onItemMove(int start, int end);
    }

}
