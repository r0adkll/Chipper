package com.r0adkll.chipper.tv.ui.leanback.playback;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.*;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;

import com.nispok.snackbar.Snackbar;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.playback.MusicService;
import com.r0adkll.chipper.playback.events.MediaSessionEvent;
import com.r0adkll.chipper.playback.events.PlayProgressEvent;
import com.r0adkll.chipper.playback.events.PlayQueueEvent;
import com.r0adkll.chipper.playback.model.PlayQueue;
import com.r0adkll.chipper.playback.model.SessionState;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.utils.prefs.IntPreference;
import com.r0adkll.chipper.qualifiers.SessionRepeatPreference;
import com.r0adkll.chipper.qualifiers.SessionShufflePreference;
import com.r0adkll.chipper.tv.ui.model.BasePlaybackOverlayFragment;
import com.r0adkll.chipper.tv.ui.model.ChiptuneDescriptionPresenter;
import com.r0adkll.chipper.tv.ui.model.ChiptunePresenter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED;

/**
 * Created by r0adkll on 12/8/14.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class TVPlaybackFragment extends BasePlaybackOverlayFragment implements TVPlaybackView{

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject
    TVPlaybackPresenter presenter;

    @Inject
    PlaylistManager playlistManager;

    @Inject
    @SessionRepeatPreference
    IntPreference mRepeatPreference;

    @Inject
    @SessionShufflePreference
    BooleanPreference mShufflePreference;

    @Inject
    Bus mBus;

//    OnPlayPauseClickedListener mCallback;
    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private ArrayObjectAdapter mSecondaryActionsAdapter;
    private ArrayObjectAdapter mQueueAdapter;

    private AddAction mAddAction;
    private PlayPauseAction mPlayPauseAction;
    private RepeatAction mRepeatAction;
    private ThumbsUpAction mThumbsUpAction;
    private ThumbsDownAction mThumbsDownAction;
    private ShuffleAction mShuffleAction;
    private SkipNextAction mSkipNextAction;
    private SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow mPlaybackControlsRow;

    private SessionState mState;
    private PlayQueue mQueue;
    private PlayProgressEvent mPlayProgress;

    private MediaSession mSession;
    private MediaController mController;

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupBackgroundManager();
        setFadingEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Setup the background mananager
     */
    private void setupBackgroundManager() {

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());

        Drawable bg = getResources().getDrawable(R.drawable.play_feature_chipper);

        backgroundManager.setDrawable(bg);
    }

    private void buildRows(){
        ClassPresenterSelector ps = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        playbackControlsRowPresenter = new PlaybackControlsRowPresenter(new ChiptuneDescriptionPresenter());

        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            public void onActionClicked(Action action) {
                if (action.getId() == mPlayPauseAction.getId()) {
                    playPause();
                } else if (action.getId() == mSkipNextAction.getId()) {
                    next();
                } else if (action.getId() == mSkipPreviousAction.getId()) {
                    prev();
                } else if (action.getId() == mShuffleAction.getId()){
                    shuffle();
                } else if (action.getId() == mRepeatAction.getId()){
                    repeat();
                } else if (action.getId() == mAddAction.getId()){
                    final Chiptune tune = mQueue.current(mState);
                    playlistManager.addToFavorites(tune);
                    showSnackBar(String.format("%s was added to %s", tune.title, "Favorites"));
                }
            }
        });

        playbackControlsRowPresenter.setSecondaryActionsHidden(false);
        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();
        addOtherRows();

        setAdapter(mRowsAdapter);

    }

    private void addPlaybackControlsRow() {
        mPlaybackControlsRow = new PlaybackControlsRow(mQueue.current(mState));
        mRowsAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow();

        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);

        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        mAddAction = new AddAction(getActivity());
        mPlayPauseAction = new PlayPauseAction(getActivity());
        mRepeatAction = new RepeatAction(getActivity());
        mThumbsUpAction = new ThumbsUpAction(getActivity());
        mThumbsDownAction = new ThumbsDownAction(getActivity());
        mShuffleAction = new ShuffleAction(getActivity());
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(getActivity());
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(getActivity());
        mPlayPauseAction.setIndex(PlayPauseAction.PAUSE);

        mSecondaryActionsAdapter.add(mThumbsUpAction);
        mSecondaryActionsAdapter.add(mThumbsDownAction);

        mPrimaryActionsAdapter.add(mSkipPreviousAction);
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        mPrimaryActionsAdapter.add(mSkipNextAction);

        mSecondaryActionsAdapter.add(mRepeatAction);
        mSecondaryActionsAdapter.add(mShuffleAction);
        mSecondaryActionsAdapter.add(mAddAction);

        mShuffleAction.setIndex(mState.isShuffleEnabled() ? ShuffleAction.ON : ShuffleAction.OFF);
        notifyChanged(mShuffleAction);

        switch (mState.getRepeatMode()){
            case SessionState.MODE_ALL:
                mRepeatAction.setIndex(RepeatAction.ALL);
                break;
            case SessionState.MODE_ONE:
                mRepeatAction.setIndex(RepeatAction.ONE);
                break;
            case SessionState.MODE_NONE:
                mRepeatAction.setIndex(RepeatAction.NONE);
                break;
        }
        notifyChanged(mRepeatAction);

    }

    private void updatePlaybackRow() {
        if(mPlayProgress != null) {
            mPlaybackControlsRow.setTotalTime(mPlayProgress.duration);
            mPlaybackControlsRow.setCurrentTime(mPlayProgress.position);
            mPlaybackControlsRow.setBufferedProgress(0);
        }
    }

    private void addOtherRows() {
        mQueueAdapter = new ArrayObjectAdapter(new ChiptunePresenter());
        HeaderItem header = new HeaderItem(0, "Play Queue", null);
        mRowsAdapter.add(new ListRow(header, mQueueAdapter));
    }

    private void updatePlayQueue(){
        if(mQueue != null && mState != null){
            mQueueAdapter.clear();
            mQueueAdapter.addAll(0, mQueue.getDisplayList(mState));
        }
    }

    public void playPause(){
        if(mController != null){
            if(mController.getPlaybackState().getPlaybackSpeed() == 0) {
                mController.getTransportControls().play();
            }else{
                mController.getTransportControls().pause();
            }
        }
    }

    public void next(){
        if(mController != null){
            mController.getTransportControls().skipToNext();
        }
    }

    public void prev(){
        if(mController != null){
            mController.getTransportControls().skipToPrevious();
        }
    }

    public void shuffle(){
        if(mController != null){

            boolean shuffle = toggleShuffle();
            Bundle params = new Bundle();
            params.putBoolean(MusicService.EXTRA_SHUFFLE, shuffle);
            mController.sendCommand(MusicService.COMMAND_SHUFFLE, params, null);
        }
    }

    public void repeat(){
        if(mController != null){

            int mode = cycleRepeatMode();
            Bundle params = new Bundle();
            params.putInt(MusicService.EXTRA_REPEAT, mode);
            mController.sendCommand(MusicService.COMMAND_REPEAT, params, null);
        }
    }


    /**
     * Cycle the repeat mode
     *
     * @return
     */
    private int cycleRepeatMode(){
        int mode = mRepeatPreference.get();
        mode++;
        if(mode > 2) mode = SessionState.MODE_NONE;
        return mode;
    }

    /**
     * Toggle the current shuffle
     * @return
     */
    private boolean toggleShuffle(){
        return !mShufflePreference.get();
    }



    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        adapter = mSecondaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    private static class AddAction extends Action{
        public AddAction(Context ctx) {
            super(R.id.plb_action_add);
            setIcon(ctx.getDrawable(R.drawable.ic_action_favorite));
            setLabel1("Add to playlist");
        }
    }


    /***********************************************************************************************
     *
     * Otto Subscriptions
     *
     */

    @Subscribe
    public void answerPlayQueueEvent(PlayQueueEvent event){
        mQueue = event.queue;
        mState = event.state;

        // Update UI
        if(mPlaybackControlsRow != null)
            mRowsAdapter.remove(mPlaybackControlsRow);

        buildRows();
        updatePlayQueue();



    }

    @Subscribe
    public void answerPlayProgressEvent(PlayProgressEvent event){
        mPlayProgress = event;
        updatePlaybackRow();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Subscribe
    public void answerMediaSessionEvent(MediaSessionEvent event){
        mSession = (MediaSession) event.session.getMediaSession();

        // Construct controller
        mController = new MediaController(getActivity(), mSession.getSessionToken());
        mController.registerCallback(new MediaController.Callback() {
            @Override
            public void onSessionDestroyed() {
                // Kill UI
                getActivity().finish();
            }

            @Override
            public void onSessionEvent(String event, Bundle extras) {
                // TODO: Handle Event

            }

            @Override
            public void onPlaybackStateChanged(PlaybackState state) {
                // TODO: Update UI
                switch (state.getState()){
                    case STATE_BUFFERING:
                        mPlaybackControlsRow.setCurrentTime(0);
                        mPlaybackControlsRow.setTotalTime(0);
                        break;
                    case STATE_PLAYING:
                        mPlayPauseAction.setIndex(PlayPauseAction.PAUSE);
                        notifyChanged(mPlayPauseAction);
                        break;
                    case STATE_PAUSED:
                        mPlayPauseAction.setIndex(PlayPauseAction.PLAY);
                        notifyChanged(mPlayPauseAction);
                        break;
                    case STATE_STOPPED:
                        // Hide the player and show the shuffle play button
                        break;
                }
            }

            @Override
            public void onMetadataChanged(MediaMetadata metadata) {
                // TODO: Update UI

            }

            @Override
            public void onQueueChanged(List<MediaSession.QueueItem> queue) {



            }

            @Override
            public void onQueueTitleChanged(CharSequence title) {

            }

            @Override
            public void onExtrasChanged(Bundle extras) {

            }

            @Override
            public void onAudioInfoChanged(MediaController.PlaybackInfo info) {

            }
        });

    }

    /***********************************************************************************************
     *
     * View Methods
     *
     */

    @Override
    public void refreshContent() {

    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.with(getActivity())
                .text(text)
                .show(getActivity());
    }

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    protected Object[] getModules() {
        return new Object[]{
            new TVPlaybackModule(this)
        };
    }
}
