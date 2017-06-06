package edu.muenchnermuseen.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import edu.muenchnermuseen.entities.Museum;
import edu.muenchnermuseen.fragments.ScreenSlidePageFragment;

/**
 * Created by sfrey on 06.06.2017.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    List<Museum> museums;

    public ScreenSlidePagerAdapter(FragmentManager fm, List<Museum> museums) {
        super(fm);
        this.museums = museums;
    }

    @Override
    public Fragment getItem(int position) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();

        Bundle args = new Bundle();
        args.putSerializable("museum", museums.get(position));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return museums.size();
    }
}