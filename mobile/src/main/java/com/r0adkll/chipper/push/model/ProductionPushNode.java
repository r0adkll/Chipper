package com.r0adkll.chipper.push.model;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.account.GoogleAccountManager;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.playback.MusicService;
import com.r0adkll.chipper.ui.Chipper;
import com.r0adkll.chipper.ui.featured.FeaturedActivity;
import com.r0adkll.chipper.ui.playlists.PlaylistActivity;

import timber.log.Timber;

import static com.r0adkll.chipper.push.PushUtils.*;

/**
 * Created by r0adkll on 12/17/14.
 */
public class ProductionPushNode implements PushNode {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    private static final int TYPE_NOTIFICATION_ID = 4000;
    private static final int TYPE_FEATURED_ID = 4001;
    private static final int TYPE_REDEEMED_ID = 4002;

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private Context mCtx;
    private NotificationManagerCompat mNotifMan;
    private User mUser;

    /**
     * Constructor
     */
    public ProductionPushNode(Context ctx,
                              User user,
                              NotificationManagerCompat notificationManager){
        mCtx = ctx;
        mUser = user;
        mNotifMan = notificationManager;
    }

    /***********************************************************************************************
     *
     * Node Methods
     *
     */

    @Override
    public void onPushMessage(String type, Bundle extras) {
        Timber.i("onPushMessage(%s, %s)", type, extras);
        switch (type){
            case TYPE_SYNC_PLAYLIST:
                syncPlaylists();
                break;
            case TYPE_SYNC_VOTES:
                syncVotes();
                break;
            case TYPE_SYNC_DEVICES:
                syncDevices();
                break;
            case TYPE_SHARED_SYNC:
                syncShared();
                break;
            case TYPE_SHARE_REDEEMED:
                showRedeemedNotification(extras);
                break;
            case TYPE_FEATURED:
                showFeaturedNotification(extras);
                break;
            case TYPE_CONFIG:
                syncConfig();
                break;
            case TYPE_NOTIFICATION:
                showNotification(extras);
                break;
        }
    }

    @Override
    public void onPushSendError(Bundle extras) {
        // Do Something
    }

    @Override
    public void onPushDeleted(Bundle extras) {
        // Do Something
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Start a synchronization with the server and your playlists
     */
    private void syncPlaylists(){
        // Generate account used for the sync adapter
        Account acct = new Account(mUser.email, GoogleAccountManager.ACCOUNT_TYPE);

        // Sync push received, cause a sync to occur
        ContentResolver.requestSync(acct, GoogleAccountManager.AUTHORITY, null);
    }

    private void syncVotes(){
        // Force load system/user votes and update local reference

    }

    private void syncDevices(){
        // Synchronize the User's devices via sync framework

    }

    private void syncShared(){
        // Synchronize the Shared Playlists with the user

    }

    private void syncConfig(){
        // Synchronize the app configuration

    }

    /**
     * Show the simple alert notification sent from GCM
     *
     * @param extras        the bundle containing the notification information
     */
    private void showNotification(Bundle extras){

        // Build notification based on sent parameters
        String title = extras.getString("title", "");
        String message = extras.getString("message", "");
        String extra = extras.getString("extras", "");

        // Begin building notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(buildPI(Chipper.class))
                .setColor(mCtx.getResources().getColor(R.color.primary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_stat_chipper)
                .setAutoCancel(true);

        // Set big textstyle if extra was sent
        if(!TextUtils.isEmpty(extra)){
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(message)
                    .bigText(extra));
        }

        // Show the notification
        mNotifMan.notify(TYPE_NOTIFICATION_ID, builder.build());
    }

    /**
     * Show the notification for the new featured playlist
     *
     * @param extras        the bundle containing the notification information
     */
    private void showFeaturedNotification(Bundle extras){

        // Build notification based on sent parameters
        String title = extras.getString("title", "");
        String message = extras.getString("message", "");

        // Begin building notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(buildPI(FeaturedActivity.class))
                .setTicker("New feature playlist available!")
                .setColor(mCtx.getResources().getColor(R.color.primary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_stat_chipper)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(message));

        // Set the action to start playing the new featured playlist
        builder.addAction(R.drawable.ic_action_play,
                "Start listening", buildMusicService(MusicService.INTENT_ACTION_COLDSTART_FEATURED));

        // Show the notification
        mNotifMan.notify(TYPE_FEATURED_ID, builder.build());
    }

    /**
     * Show the notification for the new featured playlist
     *
     * @param extras        the bundle containing the notification information
     */
    private void showRedeemedNotification(Bundle extras){

        // Build notification based on sent parameters
        String title = extras.getString("title", "");
        String message = extras.getString("message", "");

        // Begin building notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(buildPI(PlaylistActivity.class))
                .setTicker("A playlist has been redeemed!")
                .setColor(mCtx.getResources().getColor(R.color.primary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_stat_chipper)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(message));

        // Show the notification
        mNotifMan.notify(TYPE_REDEEMED_ID, builder.build());
    }

    /**
     * Build the main content pending intent
     * @return
     */
    private PendingIntent buildPI(Class<?> clazz){
        Intent intent = new Intent(mCtx, clazz);
        return PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent buildMusicService(String action){
        Intent intent = new Intent(mCtx, MusicService.class);
        intent.setAction(action);
        return PendingIntent.getService(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
