package com.selfeval.muzink.helpers;

import com.selfeval.muzink.objs.Song;

import java.util.ArrayList;
import java.util.List;

public class CurrentPlayingQueueHelper {
    public interface OnHasStartedListener {
        void hasStarted(long id);
    }
    private static OnHasStartedListener onHasStartedListener;


    public static void setCurrentSongId(long currentSongId) {
        CurrentPlayingQueueHelper.onHasStartedListener.hasStarted(currentSongId);
    }

    public CurrentPlayingQueueHelper(OnHasStartedListener onHasStartedListener) {
        CurrentPlayingQueueHelper.onHasStartedListener = onHasStartedListener;
    }
    public static boolean isIndexPlayable(int index, List<Song> queue) {
        boolean isPlayable=false;
        if(index<queue.size()) {
            isPlayable=true;
        }
        return isPlayable;
    }

    private static ArrayList<Song> songsToPlay=new ArrayList<>();
    private static int toStartWith;

    public static int getToStartWith() {
        return toStartWith;
    }

    public static void setToStartWith(int toStartWith) {
        CurrentPlayingQueueHelper.toStartWith = toStartWith;
    }

    public static ArrayList<Song> getSongsToPlay() {
        return songsToPlay;
    }

    public static void setSongsToPlay(ArrayList<Song> songsToPlay) {
        CurrentPlayingQueueHelper.songsToPlay = songsToPlay;
    }
}
