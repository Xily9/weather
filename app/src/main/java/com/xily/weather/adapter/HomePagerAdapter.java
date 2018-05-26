package com.xily.weather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xily.weather.db.CityList;
import com.xily.weather.fragment.HomePagerFragment;

import java.util.List;

public class HomePagerAdapter extends FragmentStatePagerAdapter {
    private List<CityList> cityList;

    public HomePagerAdapter(FragmentManager fm, List<CityList> cityList) {
        super(fm);
        this.cityList = cityList;
    }

    @Override
    public Fragment getItem(int position) {
        return HomePagerFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return cityList.size();
    }
}
