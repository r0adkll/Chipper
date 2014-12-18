package com.r0adkll.chipper.data.model;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.account.GoogleAccountManager;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.sync.SyncUtils;
import com.r0adkll.chipper.qualifiers.CurrentUser;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by r0adkll on 12/17/14.
 */
public class PlaylistObserver extends ContentObserver {

    @Inject
    @CurrentUser
    User mUser;

    private Account mAcct;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public PlaylistObserver(Context ctx, Handler handler) {
        super(handler);
        ChipperApp.get(ctx).inject(this);

        mAcct = new Account(mUser.email, GoogleAccountManager.ACCOUNT_TYPE);
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Timber.i("Playlist Change [%b, %s]", selfChange, uri);
        ContentResolver.requestSync(mAcct, GoogleAccountManager.AUTHORITY, new Bundle());
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }
}
