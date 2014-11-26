package com.r0adkll.chipper.playback.model;

import android.net.Uri;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.data.CashMachine;

import java.io.File;

/**
 * Recorded Audio Session
 * @author drew.heavner
 *
 */
public class AudioSession{
	
	/**
     *
	 * Variables
     *
	 */

	private String audioFile;		
	private long length;

    /**
     * Constructor
     * @param file      the file url, web or local
     */
	public AudioSession(String file){
		this.audioFile = file;
	}

    /**
     * Chiptune cache constructor
     *
     * @param cash          the cache store manager to check for local copies of the chiptune
     * @param chiptune      the chiptune to construct from
     */
    public AudioSession(CashMachine cash, Chiptune chiptune){
        File offline = cash.getOfflineFile(chiptune);
        this.audioFile = offline != null ? offline.getAbsolutePath() : chiptune.stream_url;
        this.length = chiptune.length;
    }
	
	public String getAudioFilePath(){
		return audioFile;
	}
	
	public long getLength(){
		return length;
	}
	
	public Uri getFileURI(){
		return Uri.fromFile(new File(audioFile));
	}
	
}