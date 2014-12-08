package com.r0adkll.chipper.tv.ui.leanback.search;

import android.os.Handler;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by r0adkll on 12/7/14.
 */
public class SearchPresenterImpl implements SearchPresenter {

    private ChipperSearchView mView;

    private ChiptuneProvider mProvider;
    private PlaylistManager mPlaylistManager;
    private final Handler mHandler = new Handler();
    private SearchRunnable mSearchTask;

    /**
     * Constructor
     */
    public SearchPresenterImpl(ChipperSearchView view,
                               ChiptuneProvider provider,
                               PlaylistManager playlistManager){
        mView = view;
        mProvider = provider;
        mPlaylistManager = playlistManager;
    }


    @Override
    public void search(String query) {
        mHandler.removeCallbacks(mSearchTask);
        mSearchTask = new SearchRunnable(query);
        mHandler.post(mSearchTask);
    }




    class SearchRunnable implements Runnable{
        private String mQuery = "";

        public SearchRunnable(String query){
            mQuery = query;
        }

        @Override
        public void run() {
            List<Chiptune> chiptunes = mProvider.getAllChiptunes();
            final List<Chiptune> matches = new ArrayList<>();

            for(Chiptune chiptune: chiptunes){
                if(chiptune.title.toLowerCase().contains(mQuery.toLowerCase()) ||
                        chiptune.artist.toLowerCase().contains(mQuery.toLowerCase())){
                    matches.add(chiptune);
                }
            }

            // Show Results
            mView.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView.showSearchResults(matches);
                }
            });

        }
    }
}
