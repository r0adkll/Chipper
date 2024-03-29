package com.r0adkll.chipper.ui.screens.featured;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.FeaturedChiptuneReference;
import com.r0adkll.chipper.api.model.FeaturedPlaylist;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.data.model.ModelLoader;
import com.r0adkll.chipper.ui.screens.player.MusicPlayer;
import com.r0adkll.chipper.utils.CallbackHandler;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/16/14.
 */
public class FeaturedPresenterImpl implements FeaturedPresenter {

    private FeaturedView mView;
    private ChipperService mService;
    private VoteManager mVoteManager;
    private PlaylistManager mPlaylistManager;
    private ChiptuneProvider mProvider;
    private User mUser;

    /**
     * Constructor
     *
     * @param view
     * @param service
     * @param user
     */
    public FeaturedPresenterImpl(FeaturedView view,
                                 ChipperService service,
                                 PlaylistManager playlistManager,
                                 VoteManager voteManager,
                                 ChiptuneProvider provider,
                                 User user) {
        mView = view;
        mService = service;
        mUser = user;
        mPlaylistManager = playlistManager;
        mVoteManager = voteManager;
        mProvider = provider;
    }

    @Override
    public void loadFromServer() {
        mService.getFeaturedPlaylist(new Callback<FeaturedPlaylist>() {
            @Override
            public void success(FeaturedPlaylist playlist, Response response) {
                // Update the local reference in the database
                FeaturedPlaylist featured = new Select()
                        .from(FeaturedPlaylist.class)
                        .limit(1)
                        .executeSingle();

                if(featured != null){
                    featured.update(playlist);
                }else{
                    featured = new FeaturedPlaylist();
                    featured.save();
                    featured.update(playlist);
                }

                // Initialize the loader in the ui
                mView.initializeLoader(featured);
            }

            @Override
            public void failure(RetrofitError error) {
                handleRetrofitError(error);
            }
        });
    }

    @Override
    public void onPlaySelected(FeaturedPlaylist playlist) {
        List<Chiptune> chiptunes = playlist.getChiptunes(mProvider);
        if(chiptunes != null && !chiptunes.isEmpty()){
            Chiptune chiptune = chiptunes.get(0);
            MusicPlayer.createPlayback(mView.getActivity(), chiptune, playlist);
        }
    }

    @Override
    public void onChiptuneSelected(Chiptune chiptune) {
        MusicPlayer.createPlayback(mView.getActivity(), chiptune, mView.getFeaturedPlaylist());
    }

    @Override
    public void upvoteChiptune(final Chiptune chiptune) {
        mVoteManager.upvote(chiptune, new CallbackHandler() {
            @Override
            public void onHandle(Object value) {
                Timber.i("Upvote Successful [%s, %s]", chiptune.title, chiptune.id);
                mView.refreshContent();
            }

            @Override
            public void onFailure(String msg) {
                Timber.e("Error upvoting chiptune: %s", msg);
            }
        });
    }

    @Override
    public void downvoteChiptune(final Chiptune chiptune) {
        mVoteManager.downvote(chiptune, new CallbackHandler() {
            @Override
            public void onHandle(Object value) {
                Timber.i("Downvote Successful [%s, %s]", chiptune.title, chiptune.id);
                mView.refreshContent();
            }

            @Override
            public void onFailure(String msg) {
                Timber.e("Error downvoting chiptune: %s", msg);
            }
        });
    }

    @Override
    public void favoriteChiptunes(Chiptune... chiptunes) {
        if(mPlaylistManager.addToFavorites(chiptunes)){
            mView.refreshContent();
        }
    }

    @Override
    public void addChiptunesToPlaylist(final Chiptune... chiptunes) {
        mPlaylistManager.addToPlaylist(mView.getActivity(), new CallbackHandler<Playlist>() {
            @Override
            public void onHandle(Playlist value) {
                // Success fully added, Update UI
                String text = chiptunes.length == 1 ?
                        String.format("%s was added to %s", chiptunes[0].title, value.name) :
                        String.format("%d tunes were added to %s", chiptunes.length, value.name);

                // Show snackbar
                mView.showSnackBar(text);
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
    public void offlinePlaylist(FeaturedPlaylist playlist) {
        CashMachine.offline(mView.getActivity(), playlist);
    }

    @Override
    public void sharePlaylist(FeaturedPlaylist playlist) {

    }

    @Override
    public void favoritePlaylist(FeaturedPlaylist playlist) {
        List<Chiptune> chiptunes = playlist.getChiptunes(mProvider);
        Chiptune[] tunes = new Chiptune[chiptunes.size()];
        chiptunes.toArray(tunes);

        if(mPlaylistManager.addToFavorites(tunes)){
            String text = String.format("%d added to favorites", chiptunes.size());
            mView.showSnackBar(text);
        }

    }

    @Override
    public ModelLoader<FeaturedChiptuneReference> getLoader(FeaturedPlaylist playlist) {
        From query = new Select()
                .from(FeaturedChiptuneReference.class)
                .where("playlist=?", playlist.getId());

        return new ModelLoader<>(mView.getActivity(), FeaturedChiptuneReference.class, query, true);
    }


    /**
     * Handle the retrofit error from the chipper api
     * @param error
     */
    private void handleRetrofitError(RetrofitError error){
        mView.showSnackBar(error.getLocalizedMessage());
    }
}
