package com.r0adkll.chipper.tv.ui.model;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.r0adkll.chipper.api.model.Playlist;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by r0adkll on 12/8/14.
 */
public class PlaylistDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("MM/dd/yyy 'at' HH:mm:ss");

    @Override
    protected void onBindDescription(ViewHolder vh, Object item) {
        Playlist playlist = (Playlist) item;

        vh.getTitle().setText(playlist.name);
        vh.getSubtitle().setText(String.format("Updated %s", mDateFormat.format(new Date(playlist.updated))));
    }
}
