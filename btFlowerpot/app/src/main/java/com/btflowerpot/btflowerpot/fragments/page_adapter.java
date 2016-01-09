package com.btflowerpot.btflowerpot.fragments;

/**
 * Created by Administrator on 2016/1/5.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class page_adapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public page_adapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                get_current_sensor tab1 = new get_current_sensor();
                return tab1;
            case 1:
                get_sensor_text tab2 = new get_sensor_text();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}