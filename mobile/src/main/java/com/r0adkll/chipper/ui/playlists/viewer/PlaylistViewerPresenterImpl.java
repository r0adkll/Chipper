package com.r0adkll.chipper.ui.playlists.viewer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.nispok.snackbar.Snackbar;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.VoteManager;
import com.r0adkll.chipper.data.model.ModelLoader;
import com.r0adkll.chipper.data.model.OfflineRequest;
import com.r0adkll.chipper.ui.player.MusicPlayer;
import com.r0adkll.chipper.utils.CallbackHandler;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.styles.EditTextStyle;

import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistViewerPresenterImpl implements PlaylistViewerPresenter {

    private PlaylistViewerView mView;
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
    public PlaylistViewerPresenterImpl(PlaylistViewerView view,
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
    public void onPlaySelected(Playlist playlist) {
        List<Chiptune> chiptunes = playlist.getChiptunes(mProvider);
        if(chiptunes != null && !chiptunes.isEmpty()){
            Chiptune chiptune = chiptunes.get(0);
            MusicPlayer.createPlayback(mView.getActivity(), chiptune, playlist);
        }
    }

    @Override
    public void onChiptuneSelected(Chiptune chiptune) {
        MusicPlayer.createPlayback(mView.getActivity(), chiptune, mView.getPlaylist());
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
    public void offlinePlaylist(Playlist playlist) {
        CashMachine.offline(mView.getActivity(), playlist);
    }

    @Override
    public void sharePlaylist(Playlist playlist) {

    }

    @Override
    public void submitForFeature(final Playlist playlist) {
        // Prompt the user for a feature title
        PostOffice.newMail(mView.getActivity())
                .setTitle(R.string.dialog_submit_feature_title)
                .setMessage(R.string.dialog_submit_feature_msg)
                .setStyle(
                        new EditTextStyle.Builder(mView.getActivity())
                            .setHint("Feature title")
                            .setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                            .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                @Override
                                public void onAccepted(String s) {

                                    playlist.feature_title = s;
                                    mService.updateFeaturePlaylist(playlist.toUpdateMap(), new Callback<Playlist>() {
                                        @Override
                                        public void success(Playlist updatedPlaylist, Response response) {
                                            String text = String.format("%s is now the featured playlist", playlist.name);
                                            mView.showSnackBar(text);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            mView.showSnackBar(error.getLocalizedMessage());
                                        }
                                    });

                                }
                            }).build()
                )
                .setButton(Dialog.BUTTON_POSITIVE, R.string.dialog_button_submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButton(Dialog.BUTTON_NEGATIVE, android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.primary)
                .show(mView.getActivity().getFragmentManager());
    }

    @Override
    public ModelLoader<ChiptuneReference> getLoader(Playlist playlist) {
        From query = new Select()
                .from(ChiptuneReference.class)
                .where("playlist=?", playlist.getId());

        return new ModelLoader<>(mView.getActivity(), ChiptuneReference.class, query, true);
    }
}
