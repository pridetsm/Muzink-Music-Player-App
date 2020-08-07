package com.selfeval.muzink.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.selfeval.muzink.objs.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsLoader {
    private static List<Song> allSongs=new ArrayList<>();
    /*THIS METHOD RETRIEVES ALL SONGS IN EXTERNAL STORAGE USING THE Cursor*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Song> getAllSongs(Context context) {
        if (allSongs.isEmpty()) {
            Cursor cursor = context.getContentResolver()
                    .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    allSongs.add(new Song(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                } while (cursor.moveToNext());
            }
        }
        return allSongs;
    }
}
