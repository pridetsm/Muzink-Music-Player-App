package com.selfeval.muzink.fragments.pager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.selfeval.muzink.R;
import com.selfeval.muzink.adapters.AlbumsAdapter;
import com.selfeval.muzink.fragments.navigation.AlbumNavigationFragment;
import com.selfeval.muzink.fragments.view.AlbumsViewFragment;
import com.selfeval.muzink.objs.Album;

public class AlbumsFragment extends Fragment implements AlbumsAdapter.OnAlbumClickListener,View.OnClickListener {
    private FragmentManager fragmentManager;
    private static final String ALBUMS_VIEW_FRAGMENT="first";
    private static final String ALBUM_NAVIGATION_FRAGMENT="second";
    private static AlbumsViewFragment albumsViewFragment;
    private static AlbumNavigationFragment albumNavigationFragment;
    private Bundle albumNavigationFragmentArgs;
    private int color;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.albums_fragment,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager=getChildFragmentManager();
        if(savedInstanceState==null) {
            albumNavigationFragment=new AlbumNavigationFragment();
            albumsViewFragment=new AlbumsViewFragment();
            fragmentManager.beginTransaction().replace(R.id.albumFragmentContainer,albumsViewFragment,ALBUMS_VIEW_FRAGMENT)
                    .commit();

        }else {
            if(albumNavigationFragment!=null)
            fragmentManager.beginTransaction().replace(R.id.albumFragmentContainer,albumNavigationFragment,ALBUM_NAVIGATION_FRAGMENT)
                    .commit();
        }

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }


    @Override
    public void onClick(Album album) {
        albumNavigationFragmentArgs=new Bundle();
        albumNavigationFragmentArgs.putString("BUNDLE_KEY",ALBUM_NAVIGATION_FRAGMENT);
        albumNavigationFragmentArgs.putString("ALBUM_TITLE",album.getTitle());
        albumNavigationFragmentArgs.putString("ALBUM_ARTIST",album.getArtist());
        albumNavigationFragmentArgs.putString("ALBUM_YEAR",album.getYear());
        albumNavigationFragmentArgs.putLong("ALBUM_ID",album.getID());
        if(albumNavigationFragment==null) {
            albumNavigationFragment=new AlbumNavigationFragment();
        }
        albumNavigationFragment.setArguments(albumNavigationFragmentArgs);
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.albumFragmentContainer,albumNavigationFragment,ALBUM_NAVIGATION_FRAGMENT)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.back_button:
                fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).replace(R.id.albumFragmentContainer,albumsViewFragment,ALBUMS_VIEW_FRAGMENT)
                        .commit();
                albumNavigationFragment.onDestroy();
                albumNavigationFragment=null;
                break;
        }
    }
}
