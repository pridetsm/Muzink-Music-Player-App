package com.selfeval.muzink.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListSongsAdapter extends RecyclerView.Adapter<PlayListSongsAdapter.PlayListSongHolder> {
    public  class PlayListSongHolder extends RecyclerView.ViewHolder {

        public PlayListSongHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public PlayListSongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListSongHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
