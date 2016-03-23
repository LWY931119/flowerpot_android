package com.btflowerpot.btflowerpot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.btflowerpot.btflowerpot.R;

/**
 * Created by Administrator on 2016/3/23.
 */
public class get_sensor_chart extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View v = null;
        try {
            v = inflater.inflate(R.layout.get_sensor_chart, container, false);
       }catch (Exception e){
           e.printStackTrace();
       }
        return v;

    }
}
