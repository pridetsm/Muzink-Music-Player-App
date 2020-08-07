package com.selfeval.muzink.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.selfeval.muzink.daos.PlayListDao;
import com.selfeval.muzink.daos.PlayListSongDao;
import com.selfeval.muzink.database.PlayListsDB;
import com.selfeval.muzink.entities.PlayList;
import com.selfeval.muzink.entities.PlayListSong;

import java.util.List;

public class Repository {
    private PlayListsDB database;
    private PlayListSongDao playListSongDao;
    private PlayListDao playListDao;
    private LiveData<List<PlayListSong>> allPlayListSongs;
    private LiveData<List<PlayList>> allPlaylists;
    public Repository(Context context) {
        database= PlayListsDB.getInstance(context);
        playListSongDao=database.getPlayListSongDao();
        playListDao=database.getPlayListDao();
        allPlaylists=playListDao.getAllPlayLists();
        allPlayListSongs=playListSongDao.getAllPlayListSongs();
    }

    public LiveData<List<PlayList>> getAllPlaylists() {
        return allPlaylists;
    }

    public void insertPlayListSong(PlayListSong playListSong) {
        new InsertPlayListSongAsyncTask(playListSongDao).execute(playListSong);
    }
    public void deletePlayListSong(PlayListSong playListSong) {
        new DeletePlayListSongAsyncTask(playListSongDao).execute(playListSong);
    }
    public void insertPlayList(PlayList playList) {
        new InsertPlayListAsyncTask(playListDao).execute(playList);
    }
    public void deletePlayList(PlayList playList) {
        new DeletePlayListAsyncTask(playListDao).execute(playList);
    }
    public void updatePlayList(PlayList playList) {
        new UpdatePlayListAsyncTask(playListDao).execute(playList);
    }
    public LiveData<List<PlayListSong>> getAllPlayListSongs() {
        return allPlayListSongs;
    }
    private class DeletePlayListAsyncTask extends AsyncTask<PlayList,Void,Void> {
        private PlayListDao playListDao;

        public DeletePlayListAsyncTask(PlayListDao playListDao) {
            this.playListDao = playListDao;
        }

        @Override
        protected Void doInBackground(PlayList... playLists) {
            playListDao.deletePlayList(playLists[0]);
            return null;
        }
    }

    private class InsertPlayListAsyncTask extends AsyncTask<PlayList,Void,Void> {
        private PlayListDao playListDao;

        public InsertPlayListAsyncTask(PlayListDao playListDao) {
            this.playListDao = playListDao;
        }

        @Override
        protected Void doInBackground(PlayList... playLists) {
            playListDao.insertPlayList(playLists[0]);
            return null;
        }
    }
    private class UpdatePlayListAsyncTask extends AsyncTask<PlayList,Void,Void> {
        private PlayListDao playListDao;

        public UpdatePlayListAsyncTask(PlayListDao playListDao) {
            this.playListDao = playListDao;
        }

        @Override
        protected Void doInBackground(PlayList... playLists) {
            playListDao.updatePlayList(playLists[0]);
            return null;
        }
    }
    private class DeletePlayListSongAsyncTask extends AsyncTask<PlayListSong,Void,Void> {
        private PlayListSongDao playListSongDao;

        public DeletePlayListSongAsyncTask(PlayListSongDao playListSongDao) {
            this.playListSongDao = playListSongDao;
        }
        @Override
        protected Void doInBackground(PlayListSong... playListSongs) {
            playListSongDao.deletePlayListSong(playListSongs[0]);
            return null;
        }
    }
    private class InsertPlayListSongAsyncTask extends AsyncTask<PlayListSong,Void,Void> {
        private PlayListSongDao playListSongDao;

        public InsertPlayListSongAsyncTask(PlayListSongDao playListSongDao) {
            this.playListSongDao = playListSongDao;
        }
        @Override
        protected Void doInBackground(PlayListSong... playListSongs) {
            playListSongDao.insertPlayListSong(playListSongs[0]);
            return null;
        }
    }

}
