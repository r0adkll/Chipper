package com.r0adkll.chipper.data;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.data.events.OfflineRequestCompletedEvent;
import com.r0adkll.chipper.data.model.OfflineRequest;
import com.r0adkll.chipper.ui.Chipper;
import com.r0adkll.chipper.utils.Tools;
import com.r0adkll.deadskunk.utils.ProgressInputStream;
import com.r0adkll.deadskunk.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Bus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by r0adkll on 11/11/14.
 */
public class OfflineIntentService extends IntentService {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final String EXTRA_OFFLINE_REQUEST = "extra_offline_request";
    private static final String SERVICE_NAME = "OfflineService";
    private static final String CACHE_DIRECTORY_NAME = "offline";
    private static final int NOTIFICATION_ID = 200;

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private File mCacheDir;
    private NotificationManagerCompat mNotifMan;

    @Inject
    OkHttpClient mClient;

    @Inject
    Bus mBus;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public OfflineIntentService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ChipperApp.get(this).inject(this);

        // Get the notification manager compat
        mNotifMan = NotificationManagerCompat.from(this);

        // Create the reference to the cache dir
        mCacheDir = new File(getFilesDir(), CACHE_DIRECTORY_NAME);
        mCacheDir.mkdir();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Pull offline request from the intent
        final OfflineRequest request = intent.getParcelableExtra(EXTRA_OFFLINE_REQUEST);
        if(request == null) {
            Timber.e("Please submit a valid OfflineRequest to the offline service");
            return;
        }

        // Show Initial notification
        showStartingNotification(request.getChiptunes().size());

        // Iterate through all of the chiptunes in the request and download them
        int N = request.getChiptunes().size();
        for(int i=0; i<N; i++){
            Chiptune tune = request.getChiptunes().get(i);
            downloadChiptune(tune, i, N);
        }

        // now that we are finished downloading, dismiss the notification
        showEndingNotification(N);

        // Ensure that this call runs on the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mBus.post(new OfflineRequestCompletedEvent(request.getChiptunes()));
            }
        });
    }

    /**
     * Download a chiptune
     */
    private void downloadChiptune(final Chiptune chiptune, final int index, final int size){

        File output = new File(mCacheDir, getChiptuneMetaName(chiptune));

        try {

            // First, check for existing file
            if (output.exists()) {
                // Delete the existing file
                if (output.delete()) {
                    output.createNewFile();
                }
            }else{
                output.createNewFile();
            }

            // Check for write capabilities
            if(!output.canWrite()) throw new IOException("File is not writable at this momemnt");

            // Using OkHttp, attempt to download the chiptune and save it's output file to the disk
            Request request = new Request.Builder()
                    .url(chiptune.stream_url)
                    .get()
                    .build();

            // Execute the request and get the input stream
            Response response = mClient.newCall(request).execute();
            InputStream stream = response.body().byteStream();
            BufferedInputStream bis = new BufferedInputStream(stream);
            ProgressInputStream pis = new ProgressInputStream(bis, response.body().contentLength(), new ProgressInputStream.OnProgressListener() {
                @Override
                public void onProgress(long read, long total) {
                    // Compute total progress
                    float part = 100f / (float)size;
                    float base = index * part;
                    float current = (new Long(read/2L).floatValue()/new Long(total).floatValue()) * part;
                    float progress = base + current;

                    progress = Utils.clamp(progress, 0, 100);
                    updateNotification(chiptune, index+1, size, progress);
                }
            });
            FileOutputStream fos = new FileOutputStream(output);

            // Read from the input stream
            byte[] buffer = new byte[256];
            int count = 0;
            while((count = pis.read(buffer)) > 0){
                fos.write(buffer);
            }

            // Finished writing, close streams
            pis.close();
            fos.close();

        } catch (IOException e) {
            Timber.e(e, "Error trying to offline chiptune[%s]", chiptune.title);
        }

    }

    /**
     * Show starting download notification
     * @param numDownload
     */
    private void showStartingNotification(int numDownload){

        String title = "Downloading...";
        String text = String.format("Starting download for %d chiptunes", numDownload);
        String ticker = String.format("Now downloading %d chiptunes for offline playback.", numDownload);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_stat_chipper)
                .setColor(getResources().getColor(R.color.primary))
                .setTicker(ticker)
                .setOngoing(true)
                .setProgress(0, 0, true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLocalOnly(true);

        mNotifMan.notify(NOTIFICATION_ID, builder.build());

    }

    /**
     * Show the ending notification
     *
     * @param numDownloaded
     */
    private void showEndingNotification(int numDownloaded){

        String title = "Download finished";
        String text = String.format("%d chiptunes now available for offline use", numDownloaded);
        String ticker = String.format("Finished downloading %d chiptunes", numDownloaded);

        Intent main = new Intent(this, Chipper.class);
        PendingIntent mainPi = PendingIntent.getActivity(this, 0, main, 0);

        Intent undo = new Intent(this, Chipper.class);
        PendingIntent undoPi = PendingIntent.getActivity(this, 0, undo, 0);

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.chipper_round_watch_bg))
                .addAction(new NotificationCompat.Action(R.drawable.ic_action_delete, "Undo", undoPi));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(mainPi)
                .setSmallIcon(R.drawable.ic_stat_chipper)
                .setColor(getResources().getColor(R.color.primary))
                .setTicker(ticker)
                .setOngoing(false)
                .setAutoCancel(true)
                .extend(wearableExtender )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mNotifMan.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Update the system notification with the current progress of the download
     *
     * @param currentChiptune       the chiptune to download
     * @param index                 the index in the list to download
     * @param total                 the total number of chiptunes to download
     * @param progress              the progress of the entire download
     */
    long timeSinceLastPost = 0L;
    private void updateNotification(Chiptune currentChiptune, int index, int total, float progress){

        if(Tools.timeMS() - timeSinceLastPost > 100) {

            // Prepare the notification
            String title = String.format("Downloading %s", currentChiptune.title);
            String text = String.format("%d out of %d songs. (%d%% Complete)", index, total, (int) progress);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_stat_chipper)
                    .setOngoing(true)
                    .setProgress(100, (int)progress, false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setColor(getResources().getColor(R.color.primary))
                    .setLocalOnly(true);

            // Show notification
            mNotifMan.notify(NOTIFICATION_ID, builder.build());

            // Marke the time
            timeSinceLastPost = Tools.timeMS();
        }
    }


    /**
     * Get the offline file name for a given chiptune to be used to write it to disk
     *
     * @param chiptune      the chiptune whose name to compose
     * @return              the offline meta name
     */
    private static String getChiptuneMetaName(Chiptune chiptune){
        return String.format("%s.mp3", chiptune.id);
    }

}
