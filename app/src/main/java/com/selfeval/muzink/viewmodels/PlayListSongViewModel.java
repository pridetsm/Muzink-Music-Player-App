package com.selfeval.muzink.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.selfeval.muzink.entities.PlayListSong;
import com.selfeval.muzink.repository.Repository;

import java.util.List;

public class PlayListSongViewModel extends AndroidViewModel {
    private Repository repo;
    private LiveData<List<PlayListSong>> allPlayListSongs;
    public PlayListSongViewModel(@NonNull Application application) {
        super(application);
        repo=new Repository(application);
        allPlayListSongs=repo.getAllPlayListSongs();
    }
    public void insertPlayListSong(PlayListSong playListSong) {
        repo.insertPlayListSong(playListSong);
    }
    public void deletePlayListSong(PlayListSong playListSong) {
        repo.deletePlayListSong(playListSong);
    }

    public LiveData<List<PlayListSong>> getAllPlayListSongs() {
        return allPlayListSongs;
    }
}
