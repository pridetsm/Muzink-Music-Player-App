package com.selfeval.muzink.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.selfeval.muzink.entities.PlayList;
import com.selfeval.muzink.repository.Repository;

import java.util.List;

public class PlayListViewModel extends AndroidViewModel {
    private Repository repo;
    private LiveData<List<PlayList>> allPlayLists;
    public PlayListViewModel(@NonNull Application application) {
        super(application);
        repo=new Repository(application);
        allPlayLists=repo.getAllPlaylists();
    }
    public void insertPlayList(PlayList playList) {
        repo.insertPlayList(playList);
    }
    public void deletePlayList(PlayList playList) {
        repo.deletePlayList(playList);
    }
    public void updatePlayList(PlayList playList) {
     repo.updatePlayList(playList);
    }

    public LiveData<List<PlayList>> getAllPlayLists() {
        return allPlayLists;
    }
}
