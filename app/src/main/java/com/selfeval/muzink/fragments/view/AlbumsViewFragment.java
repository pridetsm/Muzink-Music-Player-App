package com.selfeval.muzink.fragments.view;

import android.content.Context;
import android.graphics.Rect;
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
import com.selfeval.muzink.adapters.AlbumsAdapter;
import com.selfeval.muzink.fragments.pager.AlbumsFragment;

public class AlbumsViewFragment extends Fragment {
    private RecyclerView albumsRecycler;
    private AlbumsAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.albums_view_fragment,container,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumsRecycler=view.findViewById(R.id.albums_recycler);
        adapter=new AlbumsAdapter((AlbumsFragment)getParentFragment());
        albumsRecycler.setAdapter(adapter);
        albumsRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(8,12,8,0);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }
}
