package com.selfeval.muzink.activities;
/*Implementing multimple classes or inter faces can cause classdefnotfoundex
* Implementing an interface that was not introduced by the time of the version of the os was released also causes classdefnotfoundex*/
import android.content.ComponentName;
import android.graphics.Color;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.selfeval.muzink.R;
import com.selfeval.muzink.adapters.FragmentsPagerAdapter;
import com.selfeval.muzink.constants.Constants;
import com.selfeval.muzink.fragments.pager.AlbumsFragment;
import com.selfeval.muzink.fragments.pager.AllSongsFragment;
import com.selfeval.muzink.fragments.pager.PlayFragment;
import com.selfeval.muzink.fragments.pager.PlayListsFragment;
import com.selfeval.muzink.listeners.OnSongClickListener;
import com.selfeval.muzink.objs.Song;
import com.selfeval.muzink.services.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener , OnSongClickListener {
    private ViewPager pager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private MediaBrowser browser;
    private AppBarLayout appBarLayout;
    private List<Fragment> pagerFragments=new ArrayList<>();
    private FragmentsPagerAdapter pagerAdapter;
    private MediaController controller;
    private static AllSongsFragment allSongsFragment =new AllSongsFragment();
    private static AlbumsFragment albumsFragment=new AlbumsFragment();
    private static PlayFragment playFragment =new PlayFragment();
    private static PlayListsFragment playListsFragment =new PlayListsFragment();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBarLayout=findViewById(R.id.appBarLayout);
        pager=findViewById(R.id.view_pager);
        tabLayout=findViewById(R.id.tab_layout);
        toolbar=findViewById(R.id.toolbar);

        pagerAdapter=new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        initPager(pager,pagerAdapter,pagerFragments);
        initTabLayout(tabLayout);
        initToolBar(toolbar);
        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(pager);
        //initializing MediaBrowser
        browser= new MediaBrowser(this,new ComponentName(this, MusicPlayerService.class),new MediaBrowser.ConnectionCallback() {
            @Override
            public void onConnected() {
                super.onConnected();
                MediaSession.Token token=browser.getSessionToken();
                controller=new MediaController(BaseActivity.this,token);
                Log.d("MAIN ACTIVITY", "browser successfully connected");
            }
            @Override
            public void onConnectionFailed() {
                super.onConnectionFailed();
                Log.d("MAIN ACTIVITY", "browser failed to connect to service");
            }
            @Override
            public void onConnectionSuspended() {
                super.onConnectionSuspended();
                Log.d("MAIN ACTIVITY", "browser - service connection suspended");
            }
        },null);

        browser.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }

    private void initPager(ViewPager viewPager, FragmentsPagerAdapter fragmentPagerAdapter, List<Fragment> pagerFragments) {
        /*ADDING PAGER FRAGMENTS TO A LIST.*/
        //<code>init()</code> method takes an integer which the Fragment class will set as its
        //arguments.This will help determine whether a Fragment instance exist or there is need to
        //create a new one.



        pagerFragments.add(0, allSongsFragment);
        pagerFragments.add(1, albumsFragment);
        pagerFragments.add(2, playListsFragment);
        pagerFragments.add(3, playFragment);
        //
        fragmentPagerAdapter.setPagerFragments(pagerFragments);
        viewPager.setAdapter(fragmentPagerAdapter);
    }
    private void initTabLayout(TabLayout tabLayout) {
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ff888888"));
        tabLayout.setTabTextColors(Color.parseColor("#55888888"),Color.parseColor("#ff888888"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.app_bar_menu,menu);
        return true;
    }

    private void initToolBar(Toolbar toolBar ) {
        toolBar.setTitle(getResources().getString(R.string.app_name));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
      switch(v.getId()) {
          case R.id.album_play_button:
              if(v.isActivated()) {
                  controller.getTransportControls().play();
              }else {
                  controller.getTransportControls().pause();
              }
              break;
          case Constants.ALBUM_SONG_HOLDER_ID:

              controller.getTransportControls().play();
              break;


      }
    }
    public void  setSelectedTabIndicatorColor(int color) {
        tabLayout.setSelectedTabIndicatorColor(color);
    }

  
    @Override
    public void onClick(Song song) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            controller.getTransportControls().play();
        }
    }
}

