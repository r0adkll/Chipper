package com.r0adkll.chipper.ui.playlists.viewer;

import com.r0adkll.chipper.core.api.ChipperService;
import com.r0adkll.chipper.core.api.model.User;

/**
 * Created by r0adkll on 11/16/14.
 */
public class PlaylistViewerPresenterImpl implements PlaylistViewerPresenter {

    private PlaylistViewerView mView;
    private ChipperService mService;
    private User mUser;

    /**
     * Constructor
     *
     * @param view
     * @param service
     * @param user
     */
    public PlaylistViewerPresenterImpl(PlaylistViewerView view, ChipperService service, User user) {
        mView = view;
        mService = service;
        mUser = user;
    }
}
