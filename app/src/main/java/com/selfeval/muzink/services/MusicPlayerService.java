package com.selfeval.muzink.services;

import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.selfeval.muzink.helpers.CurrentPlayingQueueHelper;
import com.selfeval.muzink.objs.Song;

import java.util.ArrayList;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicPlayerService extends MediaBrowserService {
    private int mState;
    private final String TAG="MEDIA_PLAYER_SERVICE";
    private final float VOLUME_DUCK=0.2f;
    private final float VOLUME_NORMAL=1.0f;
    private List<Song> mPlayingQueue=new ArrayList<>();
    private int mCurrentIndexOnQueue;
    private MediaSession mSession;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private SessionCallback mSessionCallback;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private MediaPlayer.OnErrorListener onErrorListener;
    private MediaPlayer.OnPreparedListener onPreparedListener;
    private MediaPlayer.OnCompletionListener onCompletionListener;
    private MediaPlayer.OnMediaTimeDiscontinuityListener onMediaTimeDiscontinuityListener;




    private enum AudioFocus {
        NO_AUDIO_FOCUS_CAN_DUCK,
        NO_AUDIO_FOCUS_CAN_NOT_DUCK,
        AUDIO_FOCUS_GAINED
    }
    private AudioFocus mAudioFocus= AudioFocus.NO_AUDIO_FOCUS_CAN_NOT_DUCK;
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("DummyRoot",null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowser.MediaItem>> result) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
        setListeners();
        // Start a new MediaSession
        mSession = new MediaSession(this, "MusicService");
        setSessionToken(mSession.getSessionToken());
        mSessionCallback=new SessionCallback();
        mSession.setCallback(mSessionCallback);
        updatePlaybackState();
    }

    /*Service Methods******/

    private void handlePlayRequest() {
        Log.d(TAG, "handlePlayRequest: mState=" + mState);
        tryToGetAudioFocus();
        if (!mSession.isActive()) {
            mSession.setActive(true);
        }
        // actually play the song
        if (mState == PlaybackState.STATE_PAUSED) {
            // If we're paused, just continue playback and restore the 'foreground service' state.
            configureMediaPlayerState();
        } else {
            // If we're stopped or playing a song,
            // just go ahead to the new song and (re)start playing
            playCurrentSong();
        }
    }
    /**
     * Handle a request to stop music
     */
    private void handleStopRequest(String withError) {
        Log.d(TAG, "handleStopRequest: mState=" + mState + " error=" + withError);
        mState = PlaybackState.STATE_STOPPED;
        // let go of all resources...
        relaxResources(true);
        giveUpAudioFocus();
        updatePlaybackState();
        // mMediaNotification.stopNotification();
        // service is no longer necessary. Will be started again if needed.
        stopSelf();
    }
    private void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: mState=" + mState);
        if (mState == PlaybackState.STATE_PLAYING) {
            // Pause media player and cancel the 'foreground service' state.
            mState = PlaybackState.STATE_PAUSED;
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
            // while paused, retain the MediaPlayer but give up audio focus
            relaxResources(false);
            giveUpAudioFocus();
        }
        updatePlaybackState();
    }
    private void configureMediaPlayerState() {
        if(mAudioFocus== AudioFocus.NO_AUDIO_FOCUS_CAN_NOT_DUCK) {
            if(mState==PlaybackState.STATE_PLAYING) {
                Log.d(TAG,"configureMediaPlayerState() : NO_AUDIO_FOCUS_CAN_NOT_DUCK. HandlingPauseRequest");
                handlePauseRequest();
            }
        }else if(mAudioFocus== AudioFocus.NO_AUDIO_FOCUS_CAN_DUCK) {
            if(mMediaPlayer!=null) {
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK);
            }
        }else {
            if(mMediaPlayer!=null) {
                mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
            }
        }
        //resuming or restarting playback
        if(mMediaPlayer!=null&&!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
        mState=PlaybackState.STATE_PLAYING;
        updatePlayBackState(null);
    }
    private void updatePlayBackState(String error) {
        long position=PlaybackState.PLAYBACK_POSITION_UNKNOWN;
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()) {
            position=mMediaPlayer.getCurrentPosition();
        }
        PlaybackState.Builder stateBuilder=new PlaybackState.Builder();
        stateBuilder.setActions(getAvailableActions());
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            mState = PlaybackState.STATE_ERROR;
        }
        stateBuilder.setState(mState, position,1.0f, SystemClock.elapsedRealtime());
        if(mSession!=null) {
            mSession.setPlaybackState(stateBuilder.build());
        }
    }
    public long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |PlaybackState.ACTION_PLAY_FROM_SEARCH;
        if (mPlayingQueue == null||mPlayingQueue.isEmpty()) {
            return actions;
        }
        if (mState == PlaybackState.STATE_PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        }
        if (mCurrentIndexOnQueue > 0) {
            actions |= PlaybackState.ACTION_SKIP_TO_PREVIOUS;
        }
        if (mCurrentIndexOnQueue < mPlayingQueue.size() - 1) {
            actions |= PlaybackState.ACTION_SKIP_TO_NEXT;
        }
        return actions;
    }
    private void createMediaPlayerIfNeedBe() {
        Log.d(TAG,"createMediaPlayerIfNeedBe():needed?"+(mMediaPlayer==null));
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setOnErrorListener(onErrorListener);
        } else {
            mMediaPlayer.reset();
        }
    }
    private void playCurrentSong() {
        Song track=getCurrentPlayingMusic();
        if(track==null) {
            Log.d(TAG,"playCurrentSong():cannot find song to play on the current queue.\n"+
                    "currentIndex:"+mCurrentIndexOnQueue+"\n"+
                    "currentPlayingQueueSize"+mPlayingQueue.size());
            return;
        }
        Uri source= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,track.getSongID());
        Log.d(TAG, "playSong:  current (" + mCurrentIndexOnQueue + ") in playingQueue. " +
                " musicId=" + track.getSongID() +
                " source=" + source.toString());
        mState = PlaybackState.STATE_STOPPED;
        try {
            createMediaPlayerIfNeedBe();
            mState = PlaybackState.STATE_BUFFERING;
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(this,source);
            // Starts preparing the media player in the background. When
            // it's done, it will call our OnPreparedListener (that is,
            // the onPrepared() method on this class, since we set the
            // listener to 'this'). Until the media player is prepared,
            // we *cannot* call start() on it!
            mMediaPlayer.prepareAsync();
            updatePlaybackState();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void updatePlaybackState() {
        Log.d(TAG, "updatePlaybackState, setting session playback state to " + mState);
        long position = PlaybackState.PLAYBACK_POSITION_UNKNOWN;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            position = mMediaPlayer.getCurrentPosition();
        }
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(getAvailableActions());
        stateBuilder.setState(mState, position, 1.0f, SystemClock.elapsedRealtime());
        mSession.setPlaybackState(stateBuilder.build());
    }
    private Song getCurrentPlayingMusic() {
        if(CurrentPlayingQueueHelper.isIndexPlayable(mCurrentIndexOnQueue,mPlayingQueue)) {
            Log.d(TAG, "getCurrentPlayingMusic for musicId:"+mPlayingQueue.get(mCurrentIndexOnQueue).getSongID() );
            return mPlayingQueue.get(mCurrentIndexOnQueue);
        }
        return null;
    }
    /**
     * Try to get the system audio focus.
     * */
    void tryToGetAudioFocus() {
        Log.d(TAG, "tryToGetAudioFocus");
        if (mAudioFocus != AudioFocus.AUDIO_FOCUS_GAINED) {
            int result = mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AudioFocus.AUDIO_FOCUS_GAINED;
            }
        }
    }
    /**
     * Give up the audio focus.
     */
    void giveUpAudioFocus() {
        Log.d(TAG, "giveUpAudioFocus");
        if (mAudioFocus == AudioFocus.AUDIO_FOCUS_GAINED) {
            if (mAudioManager.abandonAudioFocus(onAudioFocusChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AudioFocus.NO_AUDIO_FOCUS_CAN_NOT_DUCK;
            }
        }
    }
    /**
     * Releases resources used by the service for playback. This includes the
     * "foreground service" status, the wake locks and possibly the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also be released or not
     */
    private void relaxResources(boolean releaseMediaPlayer) {
        Log.d(TAG, "relaxResources. releaseMediaPlayer=" + releaseMediaPlayer);
        // stop being a foreground service
        stopForeground(true);
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // we can also release the Wifi lock, if we're holding it
    }
    /*Service Methods******/
    /*FocusChangeListener******/

    /*FocusChangeListener******/

    /*MediaPlayer Listeners******/
    private void setListeners() {
        onAudioFocusChangeListener=new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                Log.d(TAG, "onAudioFocusChange. focusChange=" + focusChange);
                if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // We have gained focus:
                    mAudioFocus = AudioFocus.AUDIO_FOCUS_GAINED;
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    // We have lost focus. If we can duck (low playback volume), we can keep playing.
                    // Otherwise, we need to pause the playback.
                    boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
                    mAudioFocus = canDuck ? AudioFocus.NO_AUDIO_FOCUS_CAN_DUCK : AudioFocus.NO_AUDIO_FOCUS_CAN_NOT_DUCK;
                    // If we are playing, we need to reset media player by calling configMediaPlayerState
                    // with mAudioFocus properly set.
                    if (mState == PlaybackState.STATE_PLAYING && !canDuck) {
                        // If we don't have audio focus and can't duck, we save the information that we were playing, so that we can resume playback once we get the focus back.
                    }
                } else {
                    Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
                }
                configureMediaPlayerState();

            }
        };
        onCompletionListener=new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion from MediaPlayer");
                // The media player finished playing the current song, so we go ahead
                // and start the next.
                if (mPlayingQueue != null && !mPlayingQueue.isEmpty()) {
                    // In this sample, we restart the playing queue when it gets to the end:
                    mCurrentIndexOnQueue++;
                    if (mCurrentIndexOnQueue >= mPlayingQueue.size()) {
                        mCurrentIndexOnQueue = 0;
                    }
                    handlePlayRequest();
                } else {
                    // If there is nothing to play, we stop and release the resources:
                    handleStopRequest(null);
                }
            }
        };
        onPreparedListener=new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log .d(TAG, "onPrepared from MediaPlayer");
                // The media player is done preparing. That means we can start playing if we have audio focus.
                configureMediaPlayerState();

            }
        };
        onErrorListener=new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        };

    }





    /*MediaPlayer Listeners******/

    /*MediaSession Callback******/
    private final class SessionCallback extends MediaSession.Callback {



        @Override
        public void onPlay() {
            super.onPlay();
            mPlayingQueue=CurrentPlayingQueueHelper.getSongsToPlay();
            mCurrentIndexOnQueue=CurrentPlayingQueueHelper.getToStartWith();
            if(mPlayingQueue!=null&&!mPlayingQueue.isEmpty()) {
                handlePlayRequest();
            }

        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onPause() {
            super.onPause();
            handlePauseRequest();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }



    }
    /*MediaSession Callback******/
}
