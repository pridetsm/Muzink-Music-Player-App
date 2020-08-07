package com.selfeval.muzink.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.selfeval.muzink.constants.Constants;

import java.util.List;

public class FragmentsPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> pagerFragments;
    public FragmentsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    public void setPagerFragments(List<Fragment> pagerFragments) {
        this.pagerFragments = pagerFragments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return pagerFragments.get(position);
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.PAGE_TITLES[position];
    }
    @Override
    public int getCount() {
        return  pagerFragments!=null?pagerFragments.size():0;
    }
}
