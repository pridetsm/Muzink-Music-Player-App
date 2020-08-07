package com.selfeval.muzink.objs;

public class Song {
    private String title;
    private String artist;
    private long songID;
    private String songAlbum;
    private long albumID;
    private String duration;

    public Song(String title, String artist, long id,long albumID,String songAlbum,String duration) {
        this.title = title;
        this.artist = artist;
        this.songID = id;
        this.songAlbum=songAlbum;
        this.albumID=albumID;
        this.duration=duration;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getSongID() {
        return songID;
    }

    public long getAlbumID() {
        return albumID;
    }

    public String getDuration() {
        return duration;
    }
}
