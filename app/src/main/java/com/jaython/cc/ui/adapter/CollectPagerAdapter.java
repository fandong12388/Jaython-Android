package com.jaython.cc.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jaython.cc.ui.fragment.CollectComposeFragment;
import com.jaython.cc.ui.fragment.CollectNewsFragment;

/**
 * time: 2017/2/14
 * description:
 *
 * @author fandong
 */
public class CollectPagerAdapter extends FragmentStatePagerAdapter {

    private CollectComposeFragment mComposeFragment;
    private CollectNewsFragment mNewsFragment;

    public CollectPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mComposeFragment = CollectComposeFragment.newInstance();
        this.mNewsFragment = CollectNewsFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return mComposeFragment;
        }
        return mNewsFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
