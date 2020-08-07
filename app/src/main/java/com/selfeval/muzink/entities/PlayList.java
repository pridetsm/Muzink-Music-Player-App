package com.selfeval.muzink.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlist")
public class PlayList {

    private String name;
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    public PlayList(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
