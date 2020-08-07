package com.selfeval.muzink.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListsAdapter extends RecyclerView.Adapter<PlayListsAdapter.PlayListHolder> {
    public class PlayListHolder extends RecyclerView.ViewHolder {

        public PlayListHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
