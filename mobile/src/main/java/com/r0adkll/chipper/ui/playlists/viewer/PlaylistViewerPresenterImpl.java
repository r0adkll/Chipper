package com.r0adkll.chipper.ui.playlists.viewer;

import android.content.Intent;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.data.model.ModelLoader;
import com.r0adkll.chipper.data.model.OfflineRequest;
import com.r0adkll.chipper.utils.CallbackHandler;

import java.util.Arrays;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistViewerPresenterImpl implements PlaylistViewerPresenter {

    private PlaylistViewerView mView;
    private ChipperService mService;
    private VoteManager mVoteManager;
    private PlaylistManager mPlaylistManager;
    private User mUser;

    /**
     * Constructor
     *
     * @param view
     * @param service
     * @param user
     */
    public PlaylistViewerPresenterImpl(PlaylistViewerView view,
                                       ChipperService service,
                                       PlaylistManager playlistManager,
                                       VoteManager voteManager,
                                       User user) {
        mView = view;
        mService = service;
        mUser = user;
        mPlaylistManager = playlistManager;
        mVoteManager = voteManager;
    }

    @Override
    public void onPlaySelected(Playlist playlist) {

    }

    @Override
    public void onChiptuneSelected(Chiptune chiptune) {

    }

    @Override
    public void upvoteChiptune(Chiptune chiptune) {
        mVoteManager.upvote(chiptune, new CallbackHandler() {
            @Override
            public void onHandle(Object value) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    @Override
    public void downvoteChiptune(Chiptune chiptune) {
        mVoteManager.downvote(chiptune, new CallbackHandler() {
            @Override
            public void onHandle(Object value) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    @Override
    public void favoriteChiptunes(Chiptune... chiptunes) {
        mPlaylistManager.addToFavorites(chiptunes);
    }

    @Override
    public void addChiptunesToPlaylist(Chiptune... chiptunes) {
        mPlaylistManager.addToPlaylist(mView.getActivity(), new CallbackHandler() {
            @Override
            public void onHandle(Object value) {

            }

            @Override
            public void onFailure(String msg) {

            }
        }, chiptunes);
    }

    @Override
    public void offlineChiptunes(Chiptune... chiptunes) {
        CashMachine.offline(mView.getActivity(), chiptunes);
    }

    @Override
    public void offlinePlaylist(Playlist playlist) {
        CashMachine.offline(mView.getActivity(), playlist);
    }

    @Override
    public ModelLoader<ChiptuneReference> getLoader(Playlist playlist) {
        From query = new Select()
                .from(ChiptuneReference.class)
                .where("playlist=?", playlist.getId());

        return new ModelLoader<>(mView.getActivity(), ChiptuneReference.class, query, true);
    }
}
