package com.r0adkll.chipper.tv.ui.model;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by r0adkll on 12/8/14.
 */
public class ChiptuneDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder vh, Object item) {
        Chiptune chiptune = (Chiptune) item;

        vh.getTitle().setText(chiptune.title);
        vh.getSubtitle().setText(chiptune.artist);
        vh.getBody().setText(chiptune.getFormattedLength());
    }
}
