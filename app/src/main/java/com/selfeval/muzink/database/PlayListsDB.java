package com.selfeval.muzink.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.selfeval.muzink.daos.PlayListDao;
import com.selfeval.muzink.daos.PlayListSongDao;
import com.selfeval.muzink.entities.PlayList;
import com.selfeval.muzink.entities.PlayListSong;

@androidx.room.Database(entities ={PlayList.class, PlayListSong.class},version = 1)

public abstract class PlayListsDB extends RoomDatabase {
        private static PlayListsDB instance;
        public abstract PlayListSongDao getPlayListSongDao();
        public abstract PlayListDao getPlayListDao();
        public static synchronized PlayListsDB getInstance(Context context) {
            if(instance==null) {
                instance= Room.databaseBuilder(context, PlayListsDB.class,"app_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return instance;
        }
}
