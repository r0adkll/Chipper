package com.r0adkll.chipper.playback.model;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.FeaturedPlaylist;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.data.ChiptuneProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This object represents the play queue that represents
 * the current play queue during playback. This handles fetching the next tune, shuffling,
 * handling repeat function.-
 *
 * Created by r0adkll on 11/30/14.
 */
public class PlayQueue {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private int mIndex;
    private int mShuffleIndex;

    private List<Chiptune> mQueue = new ArrayList<>();
    private List<Chiptune> mShuffleQueue = new ArrayList<>();

    /**
     * Create a play queue from a chiptune to start playing, and the playlist it belongs to
     *
     * @param chiptune      the beginning of the play queue to play
     * @param plist         the playlist the chiptune belongs to
     */
    public PlayQueue(ChiptuneProvider provider, Chiptune chiptune, Playlist plist){
        // Fill the queues
        mQueue.addAll(plist.getChiptunes(provider));
        mShuffleQueue.addAll(mQueue);

        // Shuffle the shuffle queue
        Collections.shuffle(mShuffleQueue);

        // Find correct indexes
        mIndex = mQueue.indexOf(chiptune);

        // Ensure the starting chiptune is at the top of the shuffle list
        mShuffleQueue.remove(chiptune);
        mShuffleQueue.add(0, chiptune);
        mShuffleIndex = 0;
    }

    /**
     * Create a play queue from a chiptune to start playing, and the playlist it belongs to
     *
     * @param chiptune      the beginning of the play queue to play
     * @param plist         the playlist the chiptune belongs to
     */
    public PlayQueue(ChiptuneProvider provider, Chiptune chiptune, FeaturedPlaylist plist){
        // Fill the queues
        mQueue.addAll(plist.getChiptunes(provider));
        mShuffleQueue.addAll(mQueue);

        // Shuffle the shuffle queue
        Collections.shuffle(mShuffleQueue);

        // Find correct indexes
        mIndex = mQueue.indexOf(chiptune);

        // Ensure the starting chiptune is at the top of the shuffle list
        mShuffleQueue.remove(chiptune);
        mShuffleQueue.add(0, chiptune);
        mShuffleIndex = 0;
    }

    /**
     * Create a play queue from a chiptune to start playing, and ALL the chiptunes to play from
     *
     * @param provider      the chiptune provider to get all the available chiptunes
     * @param chiptune      the chiptune to start playing
     */
    public PlayQueue(ChiptuneProvider provider, Chiptune chiptune){
        // Fill the queues
        mQueue.addAll(provider.getAllChiptunes());
        mShuffleQueue.addAll(mQueue);

        // Shuffle the shuffle queue
        Collections.shuffle(mShuffleQueue);

        // Find correct indexes
        mIndex = mQueue.indexOf(chiptune);

        // Ensure the starting chiptune is at the top of the shuffle list
        mShuffleQueue.remove(chiptune);
        mShuffleQueue.add(0, chiptune);
        mShuffleIndex = 0;
    }

    /***********************************************************************************************
     *
     * Queue Methods
     *
     */

    /**
     * Get the current chiptune in the play queue
     *
     * @param state     the current sessionstate of the playback engine (shuffle, repeatmode, etc)
     * @return          the current chiptune in the queue
     */
    public Chiptune current(SessionState state){
        if(state.isShuffleEnabled()){
            return mShuffleQueue.get(mShuffleIndex);
        }else{
            return mQueue.get(mIndex);
        }
    }

    /**
     * Based on the SessionState get the next chiptune in line to be played for this play queue.
     *
     * @param state     the current sessionstate of the playback engine (shuffle, repeatmode, etc)
     * @param force     force the next chiptune to be played (user triggered next)
     * @return          the next chiptune to be played based on the session state, or null
     */
    public Chiptune next(SessionState state, boolean force){
        if(state.isShuffleEnabled()){
            // Depending on replay mode, get the next chiptune from the shuffle queue
            int repeatMode = state.getRepeatMode();
            if(repeatMode == SessionState.MODE_ONE && !force){
                if(mShuffleIndex != -1){
                    return mShuffleQueue.get(mShuffleIndex);
                }
            }else if(repeatMode == SessionState.MODE_ALL){
                // Increment index
                mShuffleIndex++;

                // Curb Shuffle Index
                if(mShuffleIndex < mShuffleQueue.size()){
                    fixIndexes(state);
                    return mShuffleQueue.get(mShuffleIndex);
                }else{
                    // Wrap the shuffle index around to the start (only for MODE_ALL)
                    mShuffleIndex = 0;
                    fixIndexes(state);
                    return mShuffleQueue.get(mShuffleIndex);
                }
            }

            // Now increment for a forced next or MODE_NONE next
            mShuffleIndex++;
            if(mShuffleIndex < mShuffleQueue.size()){
                fixIndexes(state);
                return mShuffleQueue.get(mShuffleIndex);
            }else{
                // Decrement index to the last element in the list
                mShuffleIndex = mShuffleQueue.size() - 1;
            }

        }else{

            // Depending on replay mode, get the next chiptune from the shuffle queue
            int repeatMode = state.getRepeatMode();
            if(repeatMode == SessionState.MODE_ONE && !force){
                if(mIndex != -1){
                    return mQueue.get(mIndex);
                }
            }else if(repeatMode == SessionState.MODE_ALL){
                // Increment index
                mIndex++;

                // Curb Shuffle Index
                if(mIndex < mQueue.size()){
                    fixIndexes(state);
                    return mQueue.get(mIndex);
                }else{
                    // Wrap the shuffle index around to the start (only for MODE_ALL)
                    mIndex = 0;
                    fixIndexes(state);
                    return mQueue.get(mIndex);
                }
            }

            // Now increment for a forced next or MODE_NONE next
            mIndex++;
            if(mIndex < mQueue.size()){
                fixIndexes(state);
                return mQueue.get(mIndex);
            }else{
                // Decrement index to the last element in the list
                mIndex = mQueue.size() - 1;
            }

        }

        return null;
    }

    /**
     * Based on the SessionState return the previous chiptune to play
     *
     * @param state     the current sessionstate of the playback engine (shuffle, repeatmode, etc)
     * @return          the previous chiptune to play, or null
     */
    public Chiptune previous(SessionState state){
        if(state.isShuffleEnabled()){
            // Decrement Index
            mShuffleIndex--;

            if(mShuffleIndex > 0){
                fixIndexes(state);
                return mShuffleQueue.get(mShuffleIndex);
            }else{
                mShuffleIndex = mShuffleQueue.size() - 1;
                fixIndexes(state);
                return mShuffleQueue.get(mShuffleIndex);
            }
        }else{
            // Decrement Index
            mIndex--;

            if(mIndex > 0){
                fixIndexes(state);
                return mQueue.get(mIndex);
            }else{
                mIndex = mQueue.size() - 1;
                fixIndexes(state);
                return mQueue.get(mIndex);
            }

        }
    }

    /**
     * Re-Shuffle the shuffle list play queue then reconcile the
     * indexes
     */
    public void shuffle(){
        // Get the current tune in the shuffle queue to be played, to reconcile it once the list
        // has been shuffled
        Chiptune current = mShuffleQueue.get(mShuffleIndex);

        // Shuffle
        Collections.shuffle(mShuffleQueue);

        // Reorder the current tune to the top of the shuffle queue and reconcile the index
        mShuffleQueue.remove(current);
        mShuffleQueue.add(0, current);
        mShuffleIndex = 0;
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Reconcile the indexes after an index change caused by the functions {@link #next(SessionState, boolean)}
     * or {@link #previous(SessionState)}
     *
     * @param state     the session state that is calling this function
     */
    private void fixIndexes(SessionState state){
        if(state.isShuffleEnabled()){
            Chiptune shuffleTune = mShuffleQueue.get(mShuffleIndex);

            // Reconcile the normal index to the now current shuffle tune
            mIndex = mQueue.indexOf(shuffleTune);

            // If -1 was found, return to 0
            if(mIndex == -1) mIndex = 0;

        }else{
            Chiptune tune = mQueue.get(mIndex);

            // Reconcile the shuffle index to the nwo current tune
            mShuffleIndex = mShuffleQueue.indexOf(tune);

            // If -1 was found, return to 0
            if(mShuffleIndex == -1) mShuffleIndex = 0;

        }
    }

    /**
     * Re-index this queue to the provided chiptune
     *
     * @param chiptune      the chiptune to re-index to
     */
    public void reIndex(Chiptune chiptune){
        mShuffleIndex = mShuffleQueue.indexOf(chiptune);
        mIndex = mQueue.indexOf(chiptune);
    }

    /**
     * Get the display list for this queue depending on the state
     *
     * @param state     the current sessionstate of the playback engine (shuffle, repeatmode, etc)
     * @return          the list to display
     */
    public List<Chiptune> getDisplayList(SessionState state){
        if(state.isShuffleEnabled()){
            List<Chiptune> list = new ArrayList<>(mShuffleQueue.subList(mShuffleIndex, mShuffleQueue.size()));
            if(state.getRepeatMode() == SessionState.MODE_ALL){
                list.addAll(mShuffleQueue.subList(0, mShuffleIndex));
            }
            return list;
        }else{
            List<Chiptune> list = new ArrayList<>(mQueue.subList(mIndex, mQueue.size()));
            if(state.getRepeatMode() == SessionState.MODE_ALL){
                list.addAll(mQueue.subList(0, mIndex));
            }
            return list;
        }
    }

}
