package com.r0adkll.chipper.core.data;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.data.events.OfflineRequestCompletedEvent;
import com.r0adkll.chipper.core.data.model.OfflineRequest;
import com.r0adkll.deadskunk.utils.ProgressInputStream;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Bus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

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
    private OkHttpClient mClient;

    @Inject
    private Bus mBus;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public OfflineIntentService() {
        super(SERVICE_NAME);

        // Get the notification manager compat
        mNotifMan = NotificationManagerCompat.from(this);

        // Create the reference to the cache dir
        mCacheDir = new File(getFilesDir(), CACHE_DIRECTORY_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Pull offline request from the intent
        OfflineRequest request = intent.getParcelableExtra(EXTRA_OFFLINE_REQUEST);
        if(request == null) {
            Timber.e("Please submit a valid OfflineRequest to the offline service");
            return;
        }

        // Iterate through all of the chiptunes in the request and download them
        int N = request.getChiptunes().size();
        for(int i=0; i<N; i++){
            Chiptune tune = request.getChiptunes().get(i);
            downloadChiptune(tune, i, N);
        }

        // now that we are finished downloading, dismiss the notification
        mNotifMan.cancel(NOTIFICATION_ID);

        // Send out offline request completed event
        mBus.post(new OfflineRequestCompletedEvent(request.getChiptunes()));
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
            }

            // Check for write capabilities
            if(!output.canWrite()) throw new IOException("File is not writable at this momemnt");

            // Using OkHttp, attempt to download the chiptune and save it's output file to the disk
            Request request = new Request.Builder()
                    .url(chiptune.streamUrl)
                    .get()
                    .build();

            // Execute the request and get the input stream
            Response response = mClient.newCall(request).execute();
            InputStream stream = response.body().byteStream();
            ProgressInputStream pis = new ProgressInputStream(stream, stream.available(), new ProgressInputStream.OnProgressListener() {
                @Override
                public void onProgress(long read, long total) {
                    // Compute total progress
                    float part = 100f / (float)size;
                    float base = index * part;
                    float current = ((float)read/(float)total) * part;
                    float progress = base + current;
                    updateNotification(chiptune, index, size, progress);
                }
            });
            FileOutputStream fos = new FileOutputStream(output);

            // Read from the input stream
            byte[] buffer = new byte[1024];
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
     * Update the system notification with the current progress of the download
     *
     * @param currentChiptune       the chiptune to download
     * @param index                 the index in the list to download
     * @param total                 the total number of chiptunes to download
     * @param progress              the progress of the entire download
     */
    private void updateNotification(Chiptune currentChiptune, int index, int total, float progress){

        // Prepare the notification
        String title = String.format("Downloading %s", currentChiptune.title);
        String text = String.format("%d out of %d songs. (%d%% Complete)", index, total, (int)progress);
        String ticker = String.format("Now downloading %d chiptunes for offline playback.", total);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_stat_chipper)
                .setOngoing(true)
                .setProgress(100, (int)(progress * 100f), false)
                .setTicker(ticker)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(getResources().getColor(R.color.primary))
                .setLocalOnly(true);

        // Show notification
        mNotifMan.notify(NOTIFICATION_ID, builder.build());
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
