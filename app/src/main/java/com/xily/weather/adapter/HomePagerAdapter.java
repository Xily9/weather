package com.xily.weather.adapter;

import android.os.Bundle;
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
        HomePagerFragment homePagerFragment = HomePagerFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("id", cityList.get(position).getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }
}
