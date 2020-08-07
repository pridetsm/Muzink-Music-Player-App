package com.selfeval.muzink.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.selfeval.muzink.entities.PlayList;

import java.util.List;

@Dao
public interface PlayListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlayList(PlayList playList);
    @Delete
    void deletePlayList(PlayList playList);
    @Update
    void updatePlayList(PlayList playList);
    @Query("SELECT * FROM playlist")
    LiveData<List<PlayList>> getAllPlayLists();
}
