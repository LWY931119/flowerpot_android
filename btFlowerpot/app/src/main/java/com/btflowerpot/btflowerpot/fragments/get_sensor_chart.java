package com.btflowerpot.btflowerpot.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.btflowerpot.btflowerpot.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2016/3/23.
 */
public class get_sensor_chart extends Fragment {
    private List<String> pot_list = new ArrayList<String>();
    private Spinner pot_spinner;
    private ArrayAdapter<String> pot_adapter;

    private List<String> sensor_list = new ArrayList<String>();
    private Spinner sensor_spinner;
    private ArrayAdapter<String> sensor_adapter;

    //Element of LineChart
    private LineChartView chart;
    private LineChartData chart_data;
    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 24;

    float[] sensorDatasTab = new float[numberOfPoints];
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;
        v = inflater.inflate(R.layout.get_sensor_chart, container, false);

        IniSpinner(v);
       //LineChart
        chart = (LineChartView) v.findViewById(R.id.seneor_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        // 初始化折线图中的值
        generateValues();

        toggleLabels();
        generateData();

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);

        resetViewport();

        return v;

    }
    /*将折线图上要显示的值放在数组里*/
    private void generateValues() {

            for (int j = 0; j < numberOfPoints; ++j) {
                sensorDatasTab[j] = (float) Math.random() * 100f;
            }
    }

    //设置图表的线
    private void generateData() {

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, sensorDatasTab[j]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor){
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        chart_data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            chart_data.setAxisXBottom(axisX);
            chart_data.setAxisYLeft(axisY);
        } else {
            chart_data.setAxisXBottom(null);
            chart_data.setAxisYLeft(null);
        }

        chart_data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(chart_data);

    }
    //给每个点旁边写上数值
    private void toggleLabels() {
        hasLabels = !hasLabels;

        if (hasLabels) {
            hasLabelForSelected = false;
            chart.setValueSelectionEnabled(hasLabelForSelected);
        }

    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 200;
        v.left = 0;
        v.right = 30;

        final Viewport v1 = new Viewport(chart.getMaximumViewport());
        v1.bottom = 0;
        v1.top = 100;
        v1.left = 0;
        v1.right = 23;
        chart.setMaximumViewport(v);//用于设定chart放大缩小的极限值
        chart.setCurrentViewport(v1);//用于设定初始的值
    }

    //折线图中的点的点击事件
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
    public void IniSpinner(View v){
        //第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
        String flowerpots = new String();
        SharedPreferences mySharedPreferences=getActivity().getSharedPreferences("UserInfo", Activity.MODE_WORLD_READABLE + Activity.MODE_WORLD_WRITEABLE);
        if(mySharedPreferences.contains("flowerpots")) {
            flowerpots = mySharedPreferences.getString("flowerpots", null);
        }
        String[] strarr = flowerpots.split(",");
        int[] flowerpot_ids = new int[strarr.length];
        pot_list.add("请选择花盆：");
        for(int i=0;i<strarr.length;i++){
            flowerpot_ids[i]=Integer.parseInt(strarr[i]);
            pot_list.add("花盆 "+flowerpot_ids[i]);
        }
        pot_spinner = (Spinner)v.findViewById(R.id.spinner);
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        pot_adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, pot_list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        pot_adapter.setDropDownViewResource(R.layout.my_spinner_drop_down_item);
        //第四步：将适配器添加到下拉列表上
        pot_spinner.setAdapter(pot_adapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        pot_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
               Log.e("get_sensor_chart", "您选择的是：" + pot_adapter.getItem(arg2));
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });
        /*下拉菜单弹出的内容选项触屏事件处理*/
        pot_spinner.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                /**
                 *
                 */
                return false;
            }
        });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
        pot_spinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });

        //第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
        sensor_list.add("请选择传感器：");
        sensor_list.add("温度");
        sensor_list.add("土壤湿度");
        sensor_list.add("光照强度");
        sensor_list.add("二氧化碳浓度");
        sensor_spinner = (Spinner)v.findViewById(R.id.spinner2);
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        sensor_adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, sensor_list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        sensor_adapter.setDropDownViewResource(R.layout.my_spinner_drop_down_item);
        //第四步：将适配器添加到下拉列表上
        sensor_spinner.setAdapter(sensor_adapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        sensor_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
                Log.e("get_sensor_chart", "您选择的是：" + sensor_adapter.getItem(arg2));
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });
        /*下拉菜单弹出的内容选项触屏事件处理*/
        sensor_spinner.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
        sensor_spinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });
    }
}
