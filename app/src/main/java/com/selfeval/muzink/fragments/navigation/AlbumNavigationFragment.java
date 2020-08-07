package com.selfeval.muzink.fragments.navigation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.selfeval.muzink.R;
import com.selfeval.muzink.activities.BaseActivity;
import com.selfeval.muzink.adapters.AlbumSongsAdapter;
import com.selfeval.muzink.helpers.CurrentPlayingQueueHelper;
import com.selfeval.muzink.objs.Album;
import com.selfeval.muzink.objs.Song;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AlbumNavigationFragment extends Fragment {
    private View.OnClickListener listener;
    private Bundle args;
    private Album currentAlbum;
    private TextView albumTitle;
    private TextView albumYear;
    private TextView albumArtist;
    private ImageView artist_img;
    private RecyclerView recyclerView;
    private Button backButton;
    private Context context;
    private AlbumSongsAdapter adapter;
    private View view;
    private boolean animationDone=false;
    private ImageView wallpaper;
    private ConstraintLayout buttonsContainer;
    private Drawable buttonsContainerBackground;
    private Button overflowMenu;
    private TextView time;
    private Button albumPlayButton;
    private Button albumShuffleButton;
    private Button albumViewArtistButton;
    private Drawable backButtonBackground;
    private Drawable overflowMenuBackground;
    private Drawable timeDrawable;
    private Drawable albumPlayButtonDrawable;
    private Drawable albumShuffleButtonDrawable;
    private Drawable albumViewArtistButtonDrawable;
    private FrameLayout timeDrawableContainer;
    private CardView albumArtistImageContainer;
    private ImageView dummyImageView1;
    private ImageView dummyImageView2;
    private FrameLayout diskCenter;
    private Drawable diskCenterDrawable;
    private Drawable albumPlayButtonDrawable2;
    private View.OnClickListener albumPlayButtonFirstClickLister;
    private View.OnClickListener albumPlayButtonSecondClickLister;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.album_navigation,container,false);
        albumTitle=view.findViewById(R.id.album_title_nav);
        albumArtist=view.findViewById(R.id.album_artist_nav);
        artist_img=view.findViewById(R.id.first_artist_img_nav);
        albumYear=view.findViewById(R.id.album_year_nav);
        backButton=view.findViewById(R.id.back_button);
        buttonsContainer=view.findViewById(R.id.buttons_layout);
        backButton.setOnClickListener((View.OnClickListener) getParentFragment());
        recyclerView=view.findViewById(R.id.album_songs_recycler);
        wallpaper=view.findViewById(R.id.wallpaper2);
        overflowMenu=view.findViewById(R.id.overflowMenu);
        time=view.findViewById(R.id.time);
        albumPlayButton =view.findViewById(R.id.album_play_button);
        albumShuffleButton =view.findViewById(R.id.shuffle_button);
        albumViewArtistButton=view.findViewById(R.id.view_artist);
        backButtonBackground=getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        overflowMenuBackground=getResources().getDrawable(R.drawable.ic_menu_logo);
        albumShuffleButtonDrawable=getResources().getDrawable(R.drawable.shuffle_icon_init);
        albumViewArtistButtonDrawable=getResources().getDrawable(R.drawable.ic_userlogo);
        timeDrawable=getResources().getDrawable(R.drawable.ic_access_time_black_24dp);
        albumPlayButtonDrawable=getResources().getDrawable(R.drawable.ic_play_play_button);
        buttonsContainerBackground=getResources().getDrawable(R.drawable.button_container_drawable);
        timeDrawableContainer=view.findViewById(R.id.timeDrawableContainer);
        albumArtistImageContainer=view.findViewById(R.id.albumArtistImageContainer);
        dummyImageView1=view.findViewById(R.id.dummyImageView1);
        dummyImageView2=view.findViewById(R.id.dummyImageView2);
        diskCenter=view.findViewById(R.id.diskCenter);
        diskCenterDrawable=getResources().getDrawable(R.drawable.circle_drawable);
        albumPlayButtonDrawable2=getResources().getDrawable(R.drawable.ic_pause);

        albumPlayButtonFirstClickLister=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumPlayButton.setBackground(albumPlayButtonDrawable2);
                CurrentPlayingQueueHelper.setSongsToPlay((ArrayList<Song>) adapter.getSongs());
                CurrentPlayingQueueHelper.setToStartWith(0);
                v.setActivated(true);
                ((View.OnClickListener)context).onClick(v);
                albumPlayButton.setOnClickListener(albumPlayButtonSecondClickLister);
            }
        };
        albumPlayButtonSecondClickLister=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumPlayButton.setBackground(albumPlayButtonDrawable);
                v.setActivated(false);
                ((View.OnClickListener)context).onClick(v);
                albumPlayButton.setOnClickListener(albumPlayButtonFirstClickLister);
            }
        };
        args=getArguments();

        if(args!=null) {

            currentAlbum=new Album(args.getString("ALBUM_TITLE"),
                    args.getString("ALBUM_ARTIST"),
                    args.getString("ALBUM_YEAR"),
                    args.getLong("ALBUM_ID"));
            albumTitle.setText(currentAlbum.getTitle());
            albumArtist.setText(currentAlbum.getArtist());
            String year=currentAlbum.getYear();
            if(year==null) {
                year="Unknown Year";
            }else {
                year=year.substring(0,4);
            }
            albumYear.setText(year);
            adapter=new AlbumSongsAdapter(getParentFragment().getContext(),currentAlbum.getID());
            hideChildViews(true);
            Picasso.with(getActivity()).load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),currentAlbum.getID())).placeholder(R.drawable.dark_icon).into(artist_img, new Callback() {

                @Override
                public void onSuccess() {
                    final Bitmap bitmap=((BitmapDrawable)artist_img.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onGenerated(@Nullable Palette palette) {
                            Palette.Swatch swatch=palette.getVibrantSwatch();
                            if(swatch==null) {
                                swatch=palette.getDominantSwatch();
                            }
                            wallpaper.setImageBitmap(Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(), bitmap.getHeight()/3));
                            wallpaper.setAlpha(0.8f);
                            setDrawableTint(Color.argb(255,Color.red(swatch.getRgb())/2,Color.green(swatch.getRgb())/2,Color.blue(swatch.getRgb())/2));
                            setChildViewColors(Color.argb(255,Color.red(swatch.getRgb())/2,Color.green(swatch.getRgb())/2,Color.blue(swatch.getRgb())/2));
                            resetDrawableBackgrounds();
                            hideChildViews(false);
                            ((BaseActivity)(context)).setSelectedTabIndicatorColor(Color.argb(255,Color.red(swatch.getRgb())/2,Color.green(swatch.getRgb())/2,Color.blue(swatch.getRgb())/2));
                            applyAnimation();

                        }
                    });
                }

                @Override
                public void onError() {
                   setDrawableTint(getResources().getColor(R.color.appPrimaryColor));
                    if(!animationDone) {
                        hideChildViews(false);
                        applyAnimation();
                    }
                }

            });
        }
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        setDrawableTint(getResources().getColor(R.color.appPrimaryColor));
        diskCenter.setAlpha(0f);
        ((BaseActivity)context).setSelectedTabIndicatorColor(getResources().getColor(R.color.appPrimaryColor));
        super.onDestroy();
    }
    private void applyAnimation() {
       albumTitle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide));
       albumArtist.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide2));
       albumYear.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide3));
       buttonsContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.expand));
       Animator animator= ObjectAnimator.ofFloat(wallpaper,"alpha",0f,1f);
       animator.setDuration(500);
       animator.addListener(new Animator.AnimatorListener() {
           @Override
           public void onAnimationStart(Animator animation) {
               artistImageAnimation();

           }

           @Override
           public void onAnimationEnd(Animator animation) {
               wallpaper.setAlpha(0.8f);
              new AnimationThread().run();
              setListeners();
           }

           @Override
           public void onAnimationCancel(Animator animation) {

           }

           @Override
           public void onAnimationRepeat(Animator animation) {

           }
       });
       if(wallpaper.getDrawable()==null) {
           wallpaper.setBackground(getResources().getDrawable(R.drawable.rectangle_background));
       }
       animator.start();
       animationDone=true;

    }
    private void setListeners() {
       albumPlayButton.setOnClickListener(albumPlayButtonFirstClickLister);
    }
    private void artistImageAnimation() {
        Animation animation=AnimationUtils.loadAnimation(getActivity(), R.anim.center_expand_fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                diskCenter.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animator animator2=ObjectAnimator.ofFloat(dummyImageView1,"alpha",dummyImageView1.getAlpha(),0f);
        animator2.setDuration(500);
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
             public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dummyImageView1.setAlpha(0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        Animator animator3=ObjectAnimator.ofFloat(dummyImageView2,"alpha",dummyImageView2.getAlpha(),0f);
        animator3.setDuration(500);
        animator3.setStartDelay(100);
        animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dummyImageView2.setAlpha(0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        Animator animator1=ObjectAnimator.ofFloat(albumArtistImageContainer,"radius",0f,200f);
        animator1.setDuration(500);
        animator1.setStartDelay(200);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                albumArtistImageContainer.setRadius(200f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.start();
        animator2.start();
        animator3.start();
        diskCenter.startAnimation(animation);

    }
    private void resetDrawableBackgrounds() {
        albumPlayButton.setBackground(albumPlayButtonDrawable);
        albumShuffleButton.setBackground(albumShuffleButtonDrawable);
        albumViewArtistButton.setBackground(albumViewArtistButtonDrawable);
        overflowMenu.setBackground(overflowMenuBackground);
        buttonsContainer.setBackground(buttonsContainerBackground);
        timeDrawableContainer.setBackground(timeDrawable);
        backButton.setBackground(backButtonBackground);
        diskCenter.setBackground(diskCenterDrawable);
    }
    private void setChildViewColors(int color) {
        albumTitle.setTextColor(color);
        albumArtist.setTextColor(color);
        albumYear.setTextColor(color);
        time.setTextColor(color);
    }
    private void setDrawableTint(int color) {
        albumPlayButtonDrawable.setTint(color);
        albumViewArtistButtonDrawable.setTint(color);
        timeDrawable.setTint(color);
        albumShuffleButtonDrawable.setTint(color);
        overflowMenuBackground.setTint(color);
        backButtonBackground.setTint(color);
        buttonsContainerBackground.setTint(color);
        diskCenterDrawable.setTint(color);
        albumPlayButtonDrawable2.setTint(color);
    }
    private void hideChildViews(boolean hide) {
        if(hide) {
            albumTitle.setAlpha(0f);
            albumArtist.setAlpha(0f);
            albumYear.setAlpha(0f);
            buttonsContainer.setAlpha(0f);
        }else {
            albumTitle.setAlpha(1f);
            albumArtist.setAlpha(1f);
            albumYear.setAlpha(1f);
            buttonsContainer.setAlpha(1f);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }
    private class AnimationThread implements Runnable {
        @Override
        public void run() {
            Animation animation=AnimationUtils.loadAnimation(getActivity(), R.anim.infinite_rotation);
            animation.setInterpolator(new LinearInterpolator());
            albumArtistImageContainer.startAnimation(animation);
        }
    }

}
