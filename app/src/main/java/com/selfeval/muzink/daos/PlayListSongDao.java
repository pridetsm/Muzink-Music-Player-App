package com.selfeval.muzink.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.selfeval.muzink.entities.PlayListSong;

import java.util.List;

@Dao
public interface PlayListSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlayListSong(PlayListSong playListSong);
    @Delete
    void deletePlayListSong(PlayListSong playListSong);
    @Query("SELECT * FROM playlist_song")
    LiveData<List<PlayListSong>> getAllPlayListSongs();

}
