package com.r0adkll.chipper.core.data;

import android.app.Application;

import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Playlist;

import java.io.File;
import java.io.FilenameFilter;

import javax.inject.Inject;


/**
 * This helper class will be solely responsible for downloading/caching/retreiving
 * chiptunes from the api, but only if the user is pro
 *
 * Created by r0adkll on 11/11/14.
 */
public class CashMachine {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    private static final String CACHE_DIRECTORY_NAME = "offline";


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
    public void offline(Chiptune chiptune){

        // TODO: Initiate a chiptune downloader that downloads the specified chiptune
        // TODO: to the offline directory



    }

    /**
     * Cache an entire playlist for offline playback
     *
     * @param playlist      the playlist to offline
     */
    public void offline(Playlist playlist){

    }

    /**
     * Return whether or not this chiptune is currently offlined or
     * not.
     *
     * @param chiptune      the chiptune to check and see if it's offlined or not
     * @return              true if the chiptune is available for offline use, or false if not.
     */
    public boolean isOffline(Chiptune chiptune){

        // Scan for it's existence in the offline directory
        File[] files = mCacheDir.listFiles(new ChiptuneFilenameFilter(chiptune));
        if(files != null){
            return files.length > 0;
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
        return String.format("%s.mp3", chiptune.id);
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

        @Override
        public boolean accept(File dir, String filename) {
            return mFilename.equals(filename);
        }
    }

}
