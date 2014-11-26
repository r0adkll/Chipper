package com.r0adkll.chipper.data;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.api.model.Vote;
import com.r0adkll.chipper.utils.Callback;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

    private ChipperService mService;
    private User mCurrentUser;

    private Map<String, Integer> mOverallVoteMap;


    /**
     * Injectable Constructor
     */
    @Inject
    public VoteManager(ChipperService service, User currentUser){
        mService = service;
        mCurrentUser = currentUser;
    }


    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    public void upvote(Chiptune... chiptunes){

    }

    public void downvote(Chiptune... chiptunes){

    }

    public int getTotalVoteValue(String chiptuneId){
        return mOverallVoteMap.get(chiptuneId);
    }

    @DebugLog
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

    public void syncUserVotes(Callback<List<Vote>> cb){
        // Download and update the local store of all the user's vote data from the server
        mService.getUserVotes(mCurrentUser.id, new retrofit.Callback<List<Vote>>() {
            @Override
            public void success(List<Vote> votes, Response response) {
                // Store Votes


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    public void syncTotalVotes(final Callback<Map<String, Integer>> cb){
        // Sync total vote data from the server
        mService.getVotes(new retrofit.Callback<Map<String, Integer>>() {
            @Override
            public void success(Map<String, Integer> voteData, Response response) {
                mOverallVoteMap.putAll(voteData);
                cb.onHandle(voteData);
            }

            @Override
            public void failure(RetrofitError error) {
                // Parse retrofit error

            }
        });

    }

    /**
     * Save all the vote data into the database
     * @param votes
     */
    @DebugLog
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
