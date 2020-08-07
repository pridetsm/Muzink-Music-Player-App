package com.selfeval.muzink.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "playlist_song",primaryKeys ={"playListID","title"} )
public class PlayListSong  {

    private String artist;
    private String songAlbum;
    private long albumID;
    private long songID;
    @NonNull
    private int playListID;
    @NonNull
    private String title;

    public PlayListSong(int playListID, String title, String artist, long songID, String songAlbum, long albumID) {
        this.playListID=playListID;
        this.title=title;
        this.artist = artist;
        this.songID = songID;
        this.songAlbum = songAlbum;
        this.albumID = albumID;
    }


    public int getPlayListID() {
        return playListID;
    }

    public void setPlayListID(int playListID) {
        this.playListID = playListID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public long getSongID() {
        return songID;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public long getAlbumID() {
        return albumID;
    }
}
