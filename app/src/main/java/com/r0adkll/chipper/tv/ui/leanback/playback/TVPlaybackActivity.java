package com.r0adkll.chipper.tv.ui.leanback.playback;

import android.app.Activity;
import android.os.Bundle;

import com.activeandroid.Model;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.tv.ui.model.BaseTVActivity;

import javax.inject.Inject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.tv.ui.leanback.playlist
 * Created by drew.heavner on 12/8/14.
 */
public class TVPlaybackActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_playback);
    }
}
