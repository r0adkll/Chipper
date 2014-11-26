package com.r0adkll.chipper.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.r0adkll.chipper.playback.model.AudioSession;

import java.io.IOException;

/**
 * This class manages playing Audio Session Objects
 * @author drew.heavner
 *
 */
public class AudioPlayer {
	private static final String TAG = "AUDIO_PLAYER";

	/**
	 * CONSTANTS
	 */
	public static final int IDLE = 0;
	public static final int INITIALIZED = 1;
	public static final int PREPARED = 2;
	public static final int STARTED = 3;
	public static final int PAUSED = 4;
	public static final int STOPPED = 5;
	public static final int COMPLETED = 6;
	public static final int END = 7;
	public static final int ERROR = 8;

	/**
	 * Variables
	 */
	private Context mCtx;
	private MediaPlayer mPlayer;
	private AudioSession mCurrentSession;
    private PlayerCallbacks mCallbacks;

	private int STATE = IDLE;

	/**
	 * Empty Constructor
	 */
	public AudioPlayer(Context ctx){
        mCtx = ctx;
    }

    /**
     * Callbacks Constructor
     *
     * @param ctx           the Context Reference
     * @param callbacks     the player callbacks
     */
    public AudioPlayer(Context ctx, PlayerCallbacks callbacks){
        this(ctx);
        setPlayerCallbacks(callbacks);
    }

    /**
     * Register your player callbacks to be called by this sysetm
     * @param callbacks
     */
    public void setPlayerCallbacks(PlayerCallbacks callbacks){
        mCallbacks = callbacks;
    }

	/**************************************************************************************************************
	 * 
	 * 	MEDIA PLAYER SESSION METHODS.
	 *  - used to prepare audio sessions, play, pause, stop, release(destroy), seek
	 * 
	 */

    /**
     * Prepare a new Audio Session
     *
     * This will prepare the MediaPlayer instance to play the audio from
     * the session object and put hte state of this machine in the 'PREPARED' state
     * from which you can call 'play()' to start playing the audio
     *
     * If you have previously prepared another audio session, you must first call 'release()' before
     * calling this method again.
     *
     * @param session	the audio session to play
     */
    public void prepareAudioSession(AudioSession session){
        prepareAudioSession(session, mPreparedListener, mCompletionListener);
    }

	/**
	 * Prepare a new Audio Session 
	 * 
	 * This will prepare the MediaPlayer instance to play the audio from
	 * the session object and put hte state of this machine in the 'PREPARED' state
	 * from which you can call 'play()' to start playing the audio
	 * 
	 * If you have previously prepared another audio session, you must first call 'release()' before
	 * calling this method again.
	 * 
	 * @param session	the audio session to play
	 */
	public void prepareAudioSession(AudioSession session, MediaPlayer.OnPreparedListener listener){
		prepareAudioSession(session, listener, mCompletionListener);
	}

	/**
	 * Prepare a new Audio Session 
	 * 
	 * This will prepare the MediaPlayer instance to play the audio from
	 * the session object and put hte state of this machine in the 'PREPARED' state
	 * from which you can call 'play()' to start playing the audio
	 * 
	 * If you have previously prepared another audio session, you must first call 'release()' before
	 * calling this method again.
	 * 
	 * @param session	the audio session to play
	 * @param completeListener the audio completion listener
	 */
	public void prepareAudioSession(AudioSession session, 
			MediaPlayer.OnPreparedListener listener, 
			MediaPlayer.OnCompletionListener completeListener){

		if(mPlayer == null){

			// Set Current Session
			mCurrentSession = session;

			// Construct Media Player
			mPlayer = new MediaPlayer();
			if(session.getAudioFilePath().contains("http://") || session.getAudioFilePath().contains("https://"))
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			// Set listeners
			mPlayer.setOnPreparedListener(listener);
			mPlayer.setOnCompletionListener(completeListener);
			mPlayer.setOnErrorListener(mErrorListener);
            mPlayer.setOnInfoListener(mInfoListener);
            mPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

			try {					
				// IDLE STATE

				// Set Player data Source
				if(session.getAudioFilePath().contains("http://") || session.getAudioFilePath().contains("https://"))
					mPlayer.setDataSource(mCtx, Uri.parse(session.getAudioFilePath()));
				else
					mPlayer.setDataSource(session.getAudioFilePath());

				// INITIALIZED STATE
				STATE = INITIALIZED;

				// Prepare the Media Player
				mPlayer.prepareAsync();

				// PREPARED STATE
				STATE = PREPARED;

			} catch (IllegalArgumentException e) {
				release();
				e.printStackTrace();
			} catch (SecurityException e) {
				release();
				e.printStackTrace();
			} catch (IllegalStateException e) {
				release();
				e.printStackTrace();
			} catch (IOException e) {
				release();
				e.printStackTrace();
			}
		}
	}



	/**
	 * Pause the playback if the media player is currently playing the
	 * audio session.
	 */
	public void pause(){
		if(null != mPlayer){
			if(STATE == STARTED || STATE == PAUSED){
				mPlayer.pause();
				STATE = PAUSED;
			}
		}
	}

	/**
	 * Stop the current playback session if the media player is in the correct state
	 * then set's the current state to 'STOPPED'
	 */
	public void stop(){
		if(null != mPlayer){
			if(STATE == PREPARED || STATE == STARTED || STATE == STOPPED || STATE == PAUSED || STATE == COMPLETED){
				mPlayer.stop();
				STATE = STOPPED;
			}
		}
	}

	/**
	 * Resume playback of the current playback session, this throws the state
	 * into 'STARTED' if invoked properly
	 */
	public void play(){
		if(null != mPlayer){
			if(STATE == PREPARED || STATE == STARTED || STATE == PAUSED || STATE == COMPLETED){
				mPlayer.start();
				STATE = STARTED;
			}
		}
	}

	/**
	 * Seek the media player to a specific point in time on
	 * the audio track
	 * @param milliseconds		the point in the audio track you wish to start playing from
	 */
	public void seekTo(int milliseconds){
		if(null != mPlayer){
			if(STATE == PREPARED || STATE == STARTED || STATE == PAUSED || STATE == COMPLETED){
				mPlayer.seekTo(milliseconds);
			}
		}
	}

	/**
	 * Destroy the media player, this relinquishes media player resources from 
	 * the player object and then nullifies itself. This will put the Player in the 'END' state which means
	 * it becomes useless until the user calls 'prepareAudioSession(...)' to prepare the session
	 */
	public void release(){
		if(null != mPlayer){
            mPlayer.reset();
			mPlayer.release();
			mPlayer = null;				// nullify the player
			mCurrentSession = null;		// nullify the current session
			STATE = END;
		}
	}

	/**************************************************************************************************************
	 * 
	 * 	MEDIA PLAYER SESSION INFO METHODS.
	 *  - methods used to gain information about the current audio session
	 * 
	 */

	/**
	 * Get the current audio session for this player
	 * @return
	 */
	public AudioSession getCurrentSession(){
		return mCurrentSession;
	}

	/**
	 * Get the current state of the audio player
	 * @return		the state constant { IDLE, INITIALIZED, PREPARED, STARTED, PAUSED, STOPPED, COMPLETED, END, ERROR }
	 */
	public int getCurrentState(){ return STATE; }
	public void setCurrentState(int val){ STATE = val;}

	/**
	 * Get the current time position of hte track in milliseconds 
	 * @return	the current time position, or -1 if this is not possible in the current state
	 */
	public int getCurrentPosition(){
		if(null != mPlayer){
			if(STATE == IDLE || 
					STATE == INITIALIZED || 
					STATE == PREPARED || 
					STATE == STARTED || 
					STATE == PAUSED || 
					STATE == STOPPED || 
					STATE == COMPLETED){

				return mPlayer.getCurrentPosition();
			}
		}
		return -1;
	}

	/**
	 * Get the total duration of the current audio session
	 * @return	the total duration in milliseconds, or -1 if session is not properly prepared
	 */
	public int getTotalDuration(){
		if(null != mPlayer){
			if(STATE == PREPARED || 
					STATE == STARTED || 
					STATE == PAUSED || 
					STATE == STOPPED || 
					STATE == COMPLETED){
				// RETURN THE DURATION
				return mPlayer.getDuration();
			}
		}
		return -1;
	}

	/**
	 * Return whether or not the media player is 
	 * currently playing or not
	 * 
	 * @return		true if playing, false if not, or player is null
	 */
	public boolean isPlaying(){
		if(null != mPlayer){
			if(STATE != ERROR){
				return mPlayer.isPlaying();
			}
		}
		return false;
	}
	
	/**
	 * Return whether the player is paused or not
	 * @return
	 */
	public boolean isPaused(){
		if(null != mPlayer){
			if(STATE == PAUSED) return true;
		}
		return false;
	}

	/**
	 * Set teh Volume of the player
	 * 
	 * @param left		the left speaker volume
	 * @param right		the right speaker volume
	 */
	public void setVolume(float left, float right){
		if(null != mPlayer){
			if(STATE != ERROR){
				mPlayer.setVolume(left, right);
			}
		}
	}
	

	/**********************************************************
	 * Inner Classes & Interfaces
	 */

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if(mCallbacks != null) mCallbacks.onPrepared(mp);
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(mCallbacks != null) mCallbacks.onCompletion(mp);
        }
    };

    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if(mCallbacks != null) mCallbacks.onSeekComplete(mp);
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if(mCallbacks != null) mCallbacks.onBufferingUpdate(mp, percent);
        }
    };

    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if(mCallbacks != null){
                return mCallbacks.onInfo(mp, what, extra);
            }else{
                return false;
            }
        }
    };

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			STATE = ERROR;
            if(mCallbacks != null){
                return mCallbacks.onError(mp, what, extra);
            }else{
                return false;
            }
		}
	};


    /**
     * This is the callback interface that contains a method for every relevent event
     * from the MediaPlayer object
     *
     */
    public static interface PlayerCallbacks{

        public boolean onError(MediaPlayer mp, int what, int extra);

        public boolean onInfo(MediaPlayer mp, int what, int extra);

        public void onPrepared(MediaPlayer mp);

        public void onCompletion(MediaPlayer mp);

        public void onSeekComplete(MediaPlayer mp);

        public void onBufferingUpdate(MediaPlayer mp, int percent);

    }

    /**
     * Abstract simple implementation of the callbacks field so the
     * user can selectively choose which methods he wants to override
     */
    public static abstract class SimplePlayerCallbacks implements PlayerCallbacks{
        @Override public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
        @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
            return false;
        }
        @Override public void onPrepared(MediaPlayer mp) {}
        @Override public void onCompletion(MediaPlayer mp) {}
        @Override public void onSeekComplete(MediaPlayer mp) {}
        @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {}
    }


}
