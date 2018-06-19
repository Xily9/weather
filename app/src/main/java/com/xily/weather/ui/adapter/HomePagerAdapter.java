package com.xily.weather.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.ui.fragment.HomePagerFragment;

import java.util.List;

public class HomePagerAdapter extends FragmentStatePagerAdapter {
    private List<CityListBean> cityList;

    public HomePagerAdapter(FragmentManager fm, List<CityListBean> cityList) {
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

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
