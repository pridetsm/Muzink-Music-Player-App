package com.selfeval.muzink.fragments.pager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.selfeval.muzink.R;
import com.selfeval.muzink.adapters.AllSongsAdapter;
import com.selfeval.muzink.objs.Song;

import java.util.ArrayList;

public class AllSongsFragment extends Fragment {
    private AllSongsAdapter adapter;
    private ArrayList<Song> allExtSongs;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.all_songs_fragment_layout,container,false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        adapter=new AllSongsAdapter(context);
    }
}
