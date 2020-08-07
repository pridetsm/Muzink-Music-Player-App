package com.selfeval.muzink.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.selfeval.muzink.objs.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumsLoader {
    private static List<Album> allAlbums=new ArrayList<>();
    /*THIS METHOD RETRIEVES ALL ALBUMS IN EXTERNAL STORAGE USING THE Cursor*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Album> getAllAlbums(Context context) {
        if (allAlbums.isEmpty()) {
            Cursor cursor = context.getContentResolver()
                    .query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    allAlbums.add(new Album(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR)),
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))
                    ));
                } while (cursor.moveToNext());
            }
        }
        return allAlbums;
    }

}
