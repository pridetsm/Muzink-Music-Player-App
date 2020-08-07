package com.selfeval.muzink.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.selfeval.muzink.R;
import com.selfeval.muzink.constants.Constants;
import com.selfeval.muzink.entities.PlayList;
import com.selfeval.muzink.entities.PlayListSong;
import com.selfeval.muzink.helpers.CurrentPlayingQueueHelper;
import com.selfeval.muzink.loaders.SongsLoader;
import com.selfeval.muzink.objs.Song;
import com.selfeval.muzink.viewmodels.PlayListSongViewModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.AlbumSongHolder> {
    private List<Song> songs=new ArrayList<>();
    private List<Song> allSongs;
    private Context context;
    private long currentAlbumID;
    private View.OnClickListener onSongClickListener;
    private List<PlayList> playLists=new ArrayList<>();
    private PlayList favourites;
    private PlayListSongViewModel model;
        @RequiresApi(api = Build.VERSION_CODES.O)
    public AlbumSongsAdapter(Context context,long currentAlbumID) {
        this.context=context;
        this.currentAlbumID=currentAlbumID;
        this.onSongClickListener=(View.OnClickListener)context;
        allSongs= SongsLoader.getAllSongs(this.context);
        model = new ViewModelProvider((ViewModelStoreOwner) context).get(PlayListSongViewModel.class);
        //
            Iterator<Song> songsItr=allSongs.iterator();
            while (songsItr.hasNext()) {
                Song song=songsItr.next();
                if(song.getAlbumID()==currentAlbumID) {
                   songs.add(song);
                }
            }
        //

    }


    public static class AlbumSongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;

        private Button menuBtn;
        private TextView duration;
        private View.OnClickListener clickListener;
        public AlbumSongHolder(@NonNull View itemView,View.OnClickListener clickListener) {
            super(itemView);
            menuBtn=itemView.findViewById(R.id.more_menu);
            title=itemView.findViewById(R.id.song_title);

            duration=itemView.findViewById(R.id.song_duration);
            this.clickListener=clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v);
        }

}
    @NonNull
    @Override
    public AlbumSongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumSongHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_2, parent, false), null);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumSongHolder holder, final int position) {
        //this code is mostly reusable (menus part)
        Uri artistArtUri= ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),songs.get(position).getAlbumID());
        holder.title.setText(songs.get(position).getTitle());

        int duration=Integer.parseInt(songs.get(position).getDuration())/1000;
        String mins=String.valueOf(duration/60);
        String secs=String.valueOf((duration%60));
        if(Integer.parseInt(secs)<10) {
            secs="0"+secs;
        }
        holder.duration.setText(mins+":"+secs);
        holder.clickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setId(Constants.ALBUM_SONG_HOLDER_ID);
                onSongClickListener.onClick(v);
                CurrentPlayingQueueHelper.setSongsToPlay((ArrayList<Song>) songs);
                CurrentPlayingQueueHelper.setToStartWith(position);
            }
        };


    }
    private void configurePopupMenu(final int position, AlbumSongHolder holder) {
        final PopupMenu menu =new PopupMenu(context,holder.menuBtn);
        MenuInflater inflater=new MenuInflater(context);
        inflater.inflate(R.menu.popup_menu,menu.getMenu());
        for(PlayList playList:playLists) {
            menu.getMenu().getItem(1).getSubMenu().add(0,playList.getId(),0,playList.getName());
        }
        holder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Iterator<PlayList> PlayListsItr=playLists.iterator();
                while(PlayListsItr.hasNext()) {
                    PlayList playList=PlayListsItr.next();
                    if(playList.getId()==-12345) {
                        favourites=playList;
                        PlayListsItr.remove();
                    }
                }

                for (PlayList playList : playLists) {
                    if (playList.getId() == item.getItemId()) {
                        model.insertPlayListSong(new PlayListSong(playList.getId(),
                                songs.get(position).getTitle(),
                                songs.get(position).getArtist(),
                                songs.get(position).getSongID(), songs.get(position).getSongAlbum(),
                                songs.get(position).getAlbumID()));
                        Toast.makeText(context, "song added to " + playList.getName(), Toast.LENGTH_SHORT).show();
                    }

                }
                if (item.getItemId() == R.id.add_to_favorites) {
                    if (favourites != null) {
                        model.insertPlayListSong(new PlayListSong(favourites.getId(),
                                songs.get(position).getTitle(),
                                songs.get(position).getArtist(),
                                songs.get(position).getSongID(),
                                songs.get(position).getSongAlbum(),
                                songs.get(position).getAlbumID()));
                        Toast.makeText(context, "song added to favourites", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                }
                return false;

            }
        });
    }

    public List<Song> getSongs() {
        return songs;
    }

    @Override
    public int getItemCount() {
        return songs==null?0:songs.size();
    }

}