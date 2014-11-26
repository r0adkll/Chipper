package com.r0adkll.chipper.ui.model;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.ui.playlists.PlaylistView;
import com.r0adkll.deadskunk.adapters.BetterListAdapter;
import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.Style;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.model
 * Created by drew.heavner on 11/25/14.
 */
public class PlaylistStyle implements Style {

    private ListView mListView;
    private View mAddPlaylistFooter;
    private PlaylistDialogAdapter mAdapter;
    private DialogInterface mDialogInterface;

    private OnPlaylistItemSelectedListener mListener;

    /**
     * Constructor
     * @param ctx       the context to construct views from
     */
    public PlaylistStyle(Context ctx, User user){
        int padding = (int) Utils.dpToPx(ctx, 16);

        // Initialize the List View
        mListView = new ListView(ctx);
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
        mListView.setPadding(0, 0, 0, padding);
        mListView.setClipToPadding(false);

        // Inflate add playlist footer
        mAddPlaylistFooter = LayoutInflater.from(ctx)
                .inflate(R.layout.layout_playlist_add_dialog_item, mListView, false);

        // Add as a footer
        mListView.addFooterView(mAddPlaylistFooter, null, false);

        // Create Adapter
        mAdapter = new PlaylistDialogAdapter(ctx, user.getPlaylists());
        mListView.setAdapter(mAdapter);

        // Set item click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListener != null) mListener.onPlaylistSelected(mDialogInterface, mAdapter.getItem(position));
            }
        });

        mAddPlaylistFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) mListener.onAddPlaylistSelected(mDialogInterface);
            }
        });

    }

    /**
     * Set the callback listener
     * @param listener      the listener that get's called on actions
     * @return              self for chaining
     */
    public PlaylistStyle setOnPlaylistItemSelectedListener(OnPlaylistItemSelectedListener listener){
        mListener = listener;
        return this;
    }

    @Override
    public View getContentView() {
        return mListView;
    }

    @Override
    public void applyDesign(Design design, int i) {
        // Nothing to do here
    }

    @Override
    public void onButtonClicked(int i, DialogInterface dialogInterface) {
        // Nothing to do here
    }

    @Override
    public void onDialogShow(Dialog dialog) {
        mDialogInterface = dialog;
    }

    /**
     * The playlist dialog item list adapter
     */
    public static class PlaylistDialogAdapter extends BetterListAdapter<Playlist>{

        public PlaylistDialogAdapter(Context context, List<Playlist> objects) {
            super(context, R.layout.layout_playlist_dialog_item, objects);
        }

        @Override
        public ViewHolder createHolder(View view) {
            return new PlaylistViewHolder(view);
        }

        @Override
        public void bindHolder(ViewHolder viewHolder, int i, Playlist playlist) {
            PlaylistViewHolder holder = (PlaylistViewHolder) viewHolder;

            // Bind data
            holder.title.setText(playlist.name);

            int count = playlist.getCount();
            holder.songCount.setText(String.format("%d song%s", count, count == 1 ? "":"s"));
        }

        /**
         * View Holder
         */
        class PlaylistViewHolder extends ViewHolder{

            @InjectView(R.id.title)         TextView title;
            @InjectView(R.id.song_count)    TextView songCount;

            public PlaylistViewHolder(View itemView){
                ButterKnife.inject(this, itemView);
            }

        }

    }


    /**
     * Callback interface listener
     */
    public static interface OnPlaylistItemSelectedListener{
        public void onPlaylistSelected(DialogInterface dialogInterface, Playlist playlist);
        public void onAddPlaylistSelected(DialogInterface dialogInterface);
    }

}
