package com.r0adkll.chipper.data;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.ftinc.kit.util.RxUtils;
import com.r0adkll.chipper.data.model.Chiptune;
import com.r0adkll.chipper.data.model.Playlist;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;


/**
 * This helper class will be solely responsible for downloading/caching/retreiving
 * chiptunes from the api, but only if the user is pro
 *
 * Created by r0adkll on 11/11/14.
 */
@Singleton
public class CashMachine {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final String CACHE_DIRECTORY_NAME = "offline";


    /***********************************************************************************************
     *
     * Variables
     *
     */

    private File mCacheDir;

    /**
     * Default Constructor
     */
    @Inject
    public CashMachine(Application app){
        // Set the offline directory that all the files should be cached to
        mCacheDir = new File(app.getFilesDir(), CACHE_DIRECTORY_NAME);
    }

    /***********************************************************************************************
     *
     * Methods
     * TODO: Implement the Observable Pattern after learning it here
     *
     */

    /**
     * Cache a chiptune for offline playback
     *
     * @param chiptune      the chiptune to offline
     */
    public static void offline(Context ctx, Chiptune chiptune){

        // Create OfflineRequest
//        OfflineRequest request = new OfflineRequest.Builder()
//                .addChiptune(chiptune)
//                .build();
//
//        Intent offlineIntent = new Intent(ctx, OfflineIntentService.class);
//        offlineIntent.putExtra(OfflineIntentService.EXTRA_OFFLINE_REQUEST, request);
//        ctx.startService(offlineIntent);

    }

    /**
     * Cache a chiptune for offline playback
     *
     * @param chiptunes      the chiptunes to offline
     */
    public static void offline(Context ctx, Chiptune... chiptunes){

        // Create OfflineRequest
//        OfflineRequest request = new OfflineRequest.Builder()
//                .addChiptunes(Arrays.asList(chiptunes))
//                .build();
//
//        Intent offlineIntent = new Intent(ctx, OfflineIntentService.class);
//        offlineIntent.putExtra(OfflineIntentService.EXTRA_OFFLINE_REQUEST, request);
//        ctx.startService(offlineIntent);

    }

    /**
     * Cache an entire playlist for offline playback
     *
     * @param playlist      the playlist to offline
     */
    public static void offline(Context ctx, Playlist playlist){

//        // Create OfflineRequest
//        OfflineRequest request = new OfflineRequest.Builder()
//                .addPlaylist(playlist)
//                .build();
//
//        Intent offlineIntent = new Intent(ctx, OfflineIntentService.class);
//        offlineIntent.putExtra(OfflineIntentService.EXTRA_OFFLINE_REQUEST, request);
//        ctx.startService(offlineIntent);

    }

    /**
     * Return whether or not this chiptune is currently offlined or
     * not.
     *
     * @param chiptune      the chiptune to check and see if it's offlined or not
     * @return              true if the chiptune is available for offline use, or false if not.
     */
    public boolean isOffline(Chiptune chiptune){
        return isOffline(chiptune.chiptuneId);
    }

    /**
     * Return whether or not this chiptune is currently offline by it's id
     *
     * @param chiptuneId        the id of the chiptune to check
     * @return                  true if offline, false otherwise
     */
    public boolean isOffline(String chiptuneId){
        // Scan for it's existence in the offline directory
        File[] files = mCacheDir.listFiles(new ChiptuneFilenameFilter(chiptuneId));
        if(files != null){
            return files.length > 0;
        }

        return false;
    }

    /**
     * Return the offline file for the given chiptune if it exists
     *
     * @param chiptune      the chiptune to check and see if it's offlined or not
     * @return              the offline file, or null if none is found
     */
    public File getOfflineFile(Chiptune chiptune){
        // Scan for it's existence in the offline directory
        File[] files = mCacheDir.listFiles(new ChiptuneFilenameFilter(chiptune));
        if(files != null){
            if(files.length > 0){
                return files[0];
            }
        }

        return null;
    }

    public Observable<List<Chiptune>>  deleteOfflineFiles(Chiptune... tunes){
        return deleteOfflineFiles(Arrays.asList(tunes));
    }

    public Observable<List<Chiptune>> deleteOfflineFiles(Collection<Chiptune> tunes){
        return Observable.from(tunes)
                .compose(RxUtils.<Chiptune>applyIOSchedulers())
                .doOnNext(new Action1<Chiptune>() {
                    @Override
                    public void call(Chiptune chiptune) {
                        delete(chiptune);
                    }
                })
                .collect(new Func0<List<Chiptune>>() {
                    @Override
                    public List<Chiptune> call() {
                        return new ArrayList<>();
                    }
                }, new Action2<List<Chiptune>, Chiptune>() {
                    @Override
                    public void call(List<Chiptune> chiptunes, Chiptune chiptune) {
                        chiptunes.add(chiptune);
                    }
                });

    }

    private boolean delete(Chiptune chiptune){
        File cache = getOfflineFile(chiptune);
        if(cache != null && cache.exists()){
            return cache.delete();
        }
        return false;
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Get the offline file name for a given chiptune to be used to write it to disk
     *
     * @param chiptune      the chiptune whose name to compose
     * @return              the offline meta name
     */
    private static String getChiptuneMetaName(Chiptune chiptune){
        return  getChiptuneMetaName(chiptune.chiptuneId);
    }

    private static String getChiptuneMetaName(String chiptuneId){
        return String.format("%s.mp3", chiptuneId);
    }


    /***********************************************************************************************
     *
     * Inner Classes
     *
     */

    /**
     * The chiptune file filter that filters out all files except a specific one.
     */
    private static class ChiptuneFilenameFilter implements FilenameFilter {

        private String mFilename;

        /**
         * Chiptune Constructor
         *
         * @param tune      the tune to filter for
         */
        public ChiptuneFilenameFilter(Chiptune tune){
            mFilename = getChiptuneMetaName(tune);
        }

        public ChiptuneFilenameFilter(String id){
            mFilename = getChiptuneMetaName(id);
        }

        @Override
        public boolean accept(File dir, String filename) {
            return mFilename.equals(filename);
        }
    }

}
