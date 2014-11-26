package com.r0adkll.chipper.ui.all;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.ApiModule;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.api.model.ChipperError;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.data.ChiptuneProvider;
import com.r0adkll.chipper.data.OfflineIntentService;
import com.r0adkll.chipper.data.PlaylistManager;
import com.r0adkll.chipper.data.model.OfflineRequest;
import com.r0adkll.chipper.ui.model.PlaylistStyle;
import com.r0adkll.chipper.utils.ChiptuneComparator;
import com.r0adkll.chipper.utils.Tools;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.styles.EditTextStyle;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by r0adkll on 11/13/14.
 */
public class ChiptunesPresenterImpl implements ChiptunesPresenter {

    private User mCurrentUser;
    private ChiptunesView mView;
    private ChipperService mService;
    private ChiptuneProvider mProvider;
    private PlaylistManager mManager;

    /**
     * Constructor
     *
     * @param view          the chipper view interface
     * @param service       the chipper API service
     */
    public ChiptunesPresenterImpl(ChiptunesView view,
                                  ChiptuneProvider provider,
                                  ChipperService service,
                                  PlaylistManager manager,
                                  User user){
        mCurrentUser = user;
        mView = view;
        mService = service;
        mProvider = provider;
        mManager = manager;
    }

    @Override
    public void loadAllChiptunes() {
        mView.showProgress();
        mProvider.loadChiptunes(new Callback<List<Chiptune>>() {
            @Override
            public void success(List<Chiptune> chiptunes, Response response) {
                mView.hideProgress();
                setChiptunes(chiptunes);
            }

            @Override
            public void failure(RetrofitError error) {
                mView.hideProgress();
                handleRetrofitError(error);
            }
        });
    }

    @Override
    public void onChiptuneSelected(Chiptune chiptune) {
        // Send Otto Event to start playing this selected chiptune
        Timber.i("Chiptune selected[%s]: %s-%s", chiptune.id, chiptune.artist, chiptune.title);
    }



    @Override
    public void upvoteChiptune(Chiptune chiptune) {
        Toast.makeText(mView.getActivity(), "Upvote: " + chiptune.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void downvoteChiptune(Chiptune chiptune) {
        Toast.makeText(mView.getActivity(), "Downvote: " + chiptune.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void favoriteChiptunes(Chiptune... chiptunes) {
        Toast.makeText(mView.getActivity(), "Favoring " + chiptunes.length + " chiptunes", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addChiptunesToPlaylist(final Chiptune... chiptunes) {

        // Show playlist selection dialog
        PlaylistStyle style = new PlaylistStyle(mView.getActivity(), mCurrentUser)
                .setOnPlaylistItemSelectedListener(new PlaylistStyle.OnPlaylistItemSelectedListener() {
                    @Override
                    public void onPlaylistSelected(DialogInterface dialog, Playlist playlist) {
                        playlist.add(chiptunes);
                        dialog.dismiss();
                    }

                    @Override
                    public void onAddPlaylistSelected(final DialogInterface dialog) {
                        // Prompt dialog for creating a new playlist
                        // Prompt user for new playlist
                        PostOffice.newMail(mView.getActivity())
                                .setTitle("New playlist")
                                .setThemeColorFromResource(R.color.primary)
                                .setStyle(new EditTextStyle.Builder(mView.getActivity())
                                        .setHint("Playlist name")
                                        .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                            @Override
                                            public void onAccepted(String s) {
                                                // Create new playlist object
                                                Playlist playlist = mManager.createPlaylist(s);
                                                if(playlist != null) {
                                                    playlist.add(chiptunes);
                                                    // Notify UI

                                                }else{
                                                    // Notify UI

                                                }

                                                // Dismiss the dialog
                                                dialog.dismiss();
                                            }
                                        }).build())
                                .showKeyboardOnDisplay(true)
                                .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.primary)
                                .setButton(Dialog.BUTTON_POSITIVE, "Create", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show(mView.getActivity().getFragmentManager());

                    }
                });

        // Create Dialog
        PostOffice.newMail(mView.getActivity())
                .setTitle("Choose a playlist")
                .setStyle(style)
                .show(mView.getActivity().getFragmentManager());
    }

    @Override
    public void offlineChiptunes(Chiptune... chiptunes) {
        CashMachine.offline(mView.getActivity(), chiptunes);
    }

    /**
     * Sort and Send the chiptune list to the view
     *
     * @param chiptunes     the list of chiptunes to display
     */
    private void setChiptunes(List<Chiptune> chiptunes){

        // 1. Sort
        Collections.sort(chiptunes, new ChiptuneComparator());

        // 2. Send to view
        mView.setChiptunes(chiptunes);

    }


    /**
     * Handle the retrofit error from the chipper api
     * @param error
     */
    private void handleRetrofitError(RetrofitError error){
        ChipperError cer = (ChipperError) error.getBodyAs(ChipperError.class);
        if(cer != null){
            Timber.e("Retrofit Error[%s] - %s", error.getMessage(), cer.technical);
            mView.showErrorMessage(cer.readable);
        }else{
            Timber.e("Retrofit Error: %s", error.getKind().toString());
            mView.showErrorMessage(error.getLocalizedMessage());
        }
    }


}
