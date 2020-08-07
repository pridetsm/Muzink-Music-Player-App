package com.selfeval.muzink.objs;

public class Album {
    private String title;
    private String artist;
    private String year;
    private long ID;

    public Album(String title, String artist, String year, long ID) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getYear() {
        return year;
    }

    public long getID() {
        return ID;
    }
}
