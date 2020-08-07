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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.selfeval.muzink.R;
import com.selfeval.muzink.entities.PlayList;
import com.selfeval.muzink.entities.PlayListSong;
import com.selfeval.muzink.loaders.SongsLoader;
import com.selfeval.muzink.objs.Song;
import com.selfeval.muzink.viewmodels.PlayListSongViewModel;
import com.selfeval.muzink.viewmodels.PlayListViewModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AllSongsAdapter extends RecyclerView.Adapter<AllSongsAdapter.SongHolder> {
    private List<Song> allSongs;
    private Context context;
    private List<PlayList> playLists=new ArrayList<>();
    private PlayList favourites;
    private PlayListSongViewModel model;
    PlayListViewModel playListViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AllSongsAdapter(Context context) {
        this.allSongs = SongsLoader.getAllSongs(context);
        this.context = context;

    }

    public void setPlayLists(List<PlayList> playLists) {
        Iterator<PlayList> itr=playLists.iterator();
        while(itr.hasNext()) {
            PlayList playList=itr.next();
            if(playList.getId()==-12345) {
                favourites=playList;
                itr.remove();
            }
        }
        this.playLists=playLists;
        notifyDataSetChanged();
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView artist;
        private ImageView artistArt;
        private TextView album;
        private TextView duration;
        private Button menuBtn;
        public SongHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.song_title);
            artist=itemView.findViewById(R.id.artist_name);
            artistArt=itemView.findViewById(R.id.artist_img);
            album=itemView.findViewById(R.id.song_album);
            duration=itemView.findViewById(R.id.song_duration);
            menuBtn=itemView.findViewById(R.id.more_menu);
        }
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, final int position) {
        Uri artistArtUri= ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),allSongs.get(position).getAlbumID());
        holder.title.setText(allSongs.get(position).getTitle());
        holder.artist.setText(allSongs.get(position).getArtist());
        holder.album.setText(allSongs.get(position).getSongAlbum());
        int duration=Integer.parseInt(allSongs.get(position).getDuration())/1000;
        String mins=String.valueOf(duration/60);
        String secs=String.valueOf((duration%60));
        if(Integer.parseInt(secs)<10) {
            secs="0"+secs;
        }
        holder.duration.setText(mins+":"+secs);
        Glide.with(context)
                .load(artistArtUri)
                .placeholder(context.getResources().getDrawable(R.drawable.song_icon))
                .into(holder.artistArt);

        final PopupMenu menu =new PopupMenu(context,holder.menuBtn);
        MenuInflater inflater=new MenuInflater(context);
        inflater.inflate(R.menu.popup_menu,menu.getMenu());
        for(PlayList playList:playLists) {
            menu.getMenu().getItem(1).getSubMenu().add(0,playList.getId(),0,playList.getName()).setIcon(R.drawable.ic_queue_music_black_24dp);
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
                model = new ViewModelProvider((ViewModelStoreOwner) context).get(PlayListSongViewModel.class);
                for (PlayList playList : playLists) {
                    if (playList.getId() == item.getItemId()) {

                        model.insertPlayListSong(new PlayListSong(playList.getId(),
                                allSongs.get(position).getTitle(),
                                allSongs.get(position).getArtist(),
                                allSongs.get(position).getSongID(), allSongs.get(position).getSongAlbum(),
                                allSongs.get(position).getAlbumID()));
                        Toast.makeText(context, "song added to " + playList.getName(), Toast.LENGTH_SHORT).show();
                    }

                }
                if (item.getItemId() == R.id.add_to_favorites) {
                    if (favourites != null) {
                        model.insertPlayListSong(new PlayListSong(favourites.getId(),
                                allSongs.get(position).getTitle(),
                                allSongs.get(position).getArtist(),
                                allSongs.get(position).getSongID(),
                                allSongs.get(position).getSongAlbum(),
                                allSongs.get(position).getAlbumID()));
                        Toast.makeText(context, "song added to favourites", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                }
                return true;
            }

        });
    }
    @Override
    public int getItemCount() {
        return allSongs==null?0:allSongs.size();
    }
}
