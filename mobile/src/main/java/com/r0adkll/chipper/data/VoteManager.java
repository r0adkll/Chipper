package com.r0adkll.chipper.data;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.api.model.Vote;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.utils.CallbackHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.data
 * Created by drew.heavner on 11/26/14.
 */
@Singleton
public class VoteManager {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    @Inject ChipperService mService;
    @Inject @CurrentUser User mCurrentUser;
    @Inject Historian mHistorian;

    private Map<String, Integer> mOverallVoteMap;


    /**
     * Injectable Constructor
     */
    @Inject
    public VoteManager(){
        mOverallVoteMap = new HashMap<>();
    }


    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Upvote a chiptune and return it's updated value
     *
     * This will automatically update the local store of the vote value as well
     * as the current total vote cache
     *
     * @param chiptune      the chiptune to upvote
     * @param cb            the callback
     */
    @DebugLog
    public void upvote(final Chiptune chiptune, final CallbackHandler cb){
        mService.vote(mCurrentUser.id, Vote.TYPE_UP, chiptune.id, new retrofit.Callback<Map<String, Object>>() {
            @Override
            public void success(Map<String, Object> voteData, Response response) {
                Map<String, Object> updatedVote = (Map<String, Object>) voteData.get("vote");
                Vote vote = new Vote(updatedVote);
                int totalValue = ((Double)voteData.get("total_vote")).intValue();

                // Update the local vote value
                Vote existing = new Select()
                        .from(Vote.class)
                        .where("tune_id=?", vote.tune_id)
                        .limit(1)
                        .executeSingle();

                if(existing != null){
                    existing.value = vote.value;
                    existing.save();
                }else{
                    vote.save();
                }

                // Update history records
                mHistorian.updateLastVoted(chiptune);

                // Update local reference of total vote value
                mOverallVoteMap.put(vote.tune_id, totalValue);

                // Handle callback
                if(cb != null) cb.onHandle(null);
            }

            @Override
            public void failure(RetrofitError error) {
                if(cb != null) cb.onFailure(error.getLocalizedMessage());
            }
        });
    }

    /**
     * Downvote and automatically update the local store/references with the returned value
     *
     * @param chiptune      the chiptune to downvote
     * @param cb            the callback
     */
    @DebugLog
    public void downvote(final Chiptune chiptune, final CallbackHandler cb){
        mService.vote(mCurrentUser.id, Vote.TYPE_DOWN, chiptune.id, new retrofit.Callback<Map<String, Object>>() {
            @Override
            public void success(Map<String, Object> voteData, Response response) {
                Map<String, Object> updatedVote = (Map<String, Object>) voteData.get("vote");
                Vote vote = new Vote(updatedVote);
                int totalValue = ((Double)voteData.get("total_vote")).intValue();

                // Update the local vote value
                Vote existing = new Select()
                        .from(Vote.class)
                        .where("tune_id=?", vote.tune_id)
                        .limit(1)
                        .executeSingle();

                if(existing != null){
                    existing.value = vote.value;
                    existing.save();
                }else{
                    vote.save();
                }

                // Update history records
                mHistorian.updateLastVoted(chiptune);

                // Update local reference of total vote value
                mOverallVoteMap.put(vote.tune_id, totalValue);

                // Handle callback
                if(cb != null) cb.onHandle(null);
            }

            @Override
            public void failure(RetrofitError error) {
                if(cb != null) cb.onFailure(error.getLocalizedMessage());
            }
        });
    }

    /**
     * Get the total vote value for a given chiptune id
     *
     * @param chiptuneId        the chiptune to get the value for
     * @return                  the total vote value as updated from the server
     */
    public int getTotalVoteValue(String chiptuneId){
        return mOverallVoteMap.get(chiptuneId);
    }

    /**
     * Get the user's vote value for a given chiptune
     *
     * @param chiptuneId        the id of the chiptune to get teh rating value for
     * @return                  the user's vote value on that chiptune, 0 if no voting data found
     */
    public int getUserVoteValue(String chiptuneId){
        Vote vote = new Select()
                .from(Vote.class)
                .where("tune_id=?", chiptuneId)
                .limit(1)
                .executeSingle();

        if(vote != null){
            return vote.value;
        }

        return 0;
    }

    /**
     * Update the user's vote data from the server, this realistically just needs to be run once
     * per app start
     *
     */
    public void syncUserVotes(){
        // Download and update the local store of all the user's vote data from the server
        mService.getUserVotes(mCurrentUser.id, new retrofit.Callback<List<Vote>>() {
            @Override
            public void success(List<Vote> votes, Response response) {
                // Store Votes
                saveVoteData(votes);

                // TODO: Signal UI

            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error synchronizing the user's vote values: %s", error.getLocalizedMessage());
            }
        });

    }

    /**
     * Fetch an updated list of all the total vote values from
     * the server
     * @param cb        the callback that will be called when data returns
     */
    public void syncTotalVotes(final CallbackHandler<Map<String, Integer>> cb){
        // Sync total vote data from the server
        mService.getVotes(new retrofit.Callback<Map<String, Integer>>() {
            @Override
            public void success(Map<String, Integer> voteData, Response response) {
                mOverallVoteMap.putAll(voteData);
                cb.onHandle(mOverallVoteMap);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error synchronizing the total vote values: %s", error.getLocalizedMessage());
                cb.onFailure(error.getLocalizedMessage());
            }
        });

    }

    /**
     * Save all the vote data into the database
     * @param votes
     */
    private void saveVoteData(List<Vote> votes){
        ActiveAndroid.beginTransaction();
        try{
            for(Vote vote: votes){

                // Attempt to find existing first
                Vote existing = new Select()
                        .from(Vote.class)
                        .where("tune_id=?", vote.tune_id)
                        .limit(1)
                        .executeSingle();

                if(existing != null){
                    existing.value = vote.value;
                    existing.save();
                    vote = existing;
                }else {
                    vote.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

    }

}
