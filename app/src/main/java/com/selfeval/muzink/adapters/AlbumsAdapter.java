package com.selfeval.muzink.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.selfeval.muzink.R;
import com.selfeval.muzink.fragments.pager.AlbumsFragment;
import com.selfeval.muzink.loaders.AlbumsLoader;
import com.selfeval.muzink.objs.Album;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumHolder> {
    private static List<Album> albums;
    private Context context;
    public interface OnAlbumClickListener {
        void onClick(Album album);
    }
    private OnAlbumClickListener albumClickListener;
    public class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView album_art;
        private View.OnClickListener listener;
        public AlbumHolder(@NonNull View itemView,View.OnClickListener listener) {
            super(itemView);
            title=itemView.findViewById(R.id.album_title);
            album_art=itemView.findViewById(R.id.first_artist_img);
            itemView.setOnClickListener(this);
            this.listener=listener;
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AlbumsAdapter(AlbumsFragment albumsFragment) {
        this.context=albumsFragment.getContext();
        this.albumClickListener =(OnAlbumClickListener)albumsFragment;
        albums=AlbumsLoader.getAllAlbums(this.context);

    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item,parent,false),null);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, final int position) {
        holder.title.setText(albums.get(position).getTitle());
        Glide.with(context)
                .load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),albums.get(position).getID()))
                .placeholder(context.getResources().getDrawable(R.drawable.ic_album_icon))
                .into(holder.album_art);
        holder.listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               albumClickListener.onClick(albums.get(position));
            }
        };
    }
    @Override
    public int getItemCount() {
        return albums==null?0:albums.size();
    }
}
