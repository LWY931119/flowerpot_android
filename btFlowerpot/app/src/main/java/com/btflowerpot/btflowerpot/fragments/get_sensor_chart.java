package com.btflowerpot.btflowerpot.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.btflowerpot.btflowerpot.LoginActivity;
import com.btflowerpot.btflowerpot.R;
import com.btflowerpot.btflowerpot.netWork.connection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
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
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private GetDatasTask mAuthTask = null;
    private View mProgressView;
    private View mChartFormView;

    private ImageButton LeftButton;
    private ImageButton RightButton;
    private List<String> pot_list = new ArrayList<String>();
    private Spinner pot_spinner;
    private ArrayAdapter<String> pot_adapter;

    private List<String> sensor_list = new ArrayList<String>();
    private Spinner sensor_spinner;
    private ArrayAdapter<String> sensor_adapter;

    private TextView chart_time;
    //Element of LineChart
    private LineChartView chart;
    private LineChartData chart_data;
    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 24;

    private float[] sensorDatasTab = new float[numberOfPoints];
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

    private int[] flowerpot_ids = new int[100];//存下该用户的花盆id号，最大只有100个
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences mySharedPreferences=getActivity().getSharedPreferences("UserInfo", Activity.MODE_WORLD_READABLE + Activity.MODE_WORLD_WRITEABLE);
        if(!mySharedPreferences.contains("name")) {
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
        View v = null;
        v = inflater.inflate(R.layout.get_sensor_chart, container, false);
        mProgressView = v.findViewById(R.id.chart_progress);
        mChartFormView = v.findViewById(R.id.get_sensor_chart_view);
        chart_time = (TextView)v.findViewById(R.id.chart_time);
        Calendar calendar = Calendar.getInstance();
        Date mTime = calendar.getTime();
        String mtime = df.format(mTime);
        chart_time.setText(mtime);
        IniSpinner(v);
       //LineChart
        chart = (LineChartView) v.findViewById(R.id.seneor_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        // 初始化折线图中的值
        toggleLabels();
        generateData("Y",0);

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);

        resetViewport(0,100,0,23);

        LeftButton = (ImageButton)v.findViewById(R.id.Leftbtn);
        RightButton = (ImageButton)v.findViewById(R.id.Rightbtn);

        LeftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String CurrentTimeStr = chart_time.getText().toString();

                Date CurrentTimeDate;
                Calendar CurrentTime;
                try{
                    CurrentTimeDate = df.parse(CurrentTimeStr);
                    CurrentTime = Calendar.getInstance();
                    CurrentTime.setTime(CurrentTimeDate);
                    CurrentTime.add(Calendar.DAY_OF_MONTH, -1);
                    Date mTime = CurrentTime.getTime();
                    String mtime = df.format(mTime);
                    chart_time.setText(mtime);
                    if(pot_spinner.getSelectedItemId() != 0 && sensor_spinner.getSelectedItemId() != 0){
                        String CurrentTimeStr1 = chart_time.getText().toString();
                        int mpot_id = flowerpot_ids[(int) pot_spinner.getSelectedItemId()-1];
                        showProgress(true);
                        mAuthTask = new GetDatasTask(CurrentTimeStr1,mpot_id, (int)sensor_spinner.getSelectedItemId());
                        mAuthTask.execute((Void) null);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                //showProgress(true);
                //mAuthTask = new GetDatasTask();
                //mAuthTask.execute((Void) null);

            }
        });

        RightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String CurrentTimeStr = chart_time.getText().toString();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date CurrentTimeDate;
                Calendar CurrentTime;
                try{
                    CurrentTimeDate = df.parse(CurrentTimeStr);
                    CurrentTime = Calendar.getInstance();
                    CurrentTime.setTime(CurrentTimeDate);
                    CurrentTime.add(Calendar.DAY_OF_MONTH, 1);
                    Date mTime = CurrentTime.getTime();
                    String mtime = df.format(mTime);
                    chart_time.setText(mtime);
                    if(pot_spinner.getSelectedItemId() != 0 && sensor_spinner.getSelectedItemId() != 0){
                        String CurrentTimeStr1 = chart_time.getText().toString();
                        int mpot_id = flowerpot_ids[(int) pot_spinner.getSelectedItemId()-1];
                        showProgress(true);
                        mAuthTask = new GetDatasTask(CurrentTimeStr1,mpot_id, (int)sensor_spinner.getSelectedItemId());
                        mAuthTask.execute((Void) null);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                //showProgress(true);
                //mAuthTask = new GetDatasTask();
                //mAuthTask.execute((Void) null);

            }
        });
        return v;

    }

    //设置图表的线
    private void generateData(String AxisYName,int left) {

        List<Line> lines = new ArrayList<Line>();
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, sensorDatasTab[j]));
                axisValuesX.add(new AxisValue(j).setLabel(j+left+""));//添加X轴显示的刻度值
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

            if (hasAxesNames) {
                axisX.setName("小时");
                axisY.setName(AxisYName);
                axisX.setValues(axisValuesX);
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
    //设置：给每个点旁边写上数值
    private void toggleLabels() {
        hasLabels = !hasLabels;

        if (hasLabels) {
            hasLabelForSelected = false;
            chart.setValueSelectionEnabled(hasLabelForSelected);
        }

    }

    private void resetViewport(int bottom ,int topMax,int left,int Right) {

        final Viewport v1 = new Viewport(chart.getMaximumViewport());
        v1.bottom = bottom;
        v1.top = topMax;
        v1.left = 0;
        v1.right = 23;
        chart.setMaximumViewport(v1);//用于设定chart放大缩小的极限值
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
        Log.e("get_sensor_chart", "-------------IniSpinner----------");
        String flowerpots = new String();
        SharedPreferences mySharedPreferences=getActivity().getSharedPreferences("UserInfo", Activity.MODE_WORLD_READABLE + Activity.MODE_WORLD_WRITEABLE);

        flowerpots = mySharedPreferences.getString("flowerpots", null);
        pot_list.add("请选择花盆：");
        if(flowerpots != null && flowerpots != ""){
            Log.e("get_sensor_chart", "-------------IniSpinner----------");
            String[] strarr = flowerpots.split(",");
            //flowerpot_ids = new int[strarr.length];
            for(int i=0;i<strarr.length;i++){
                flowerpot_ids[i]=Integer.parseInt(strarr[i]);
                pot_list.add("花盆 "+flowerpot_ids[i]);
            }
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
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
                /* 将所选mySpinner 的值带入myTextView 中*/


                Log.e("get_sensor_chart", "您选择的是：" + pot_adapter.getItem(arg2) );
                if (pot_spinner.getSelectedItemId() != 0 && sensor_spinner.getSelectedItemId() != 0) {
                        String CurrentTimeStr = chart_time.getText().toString();
                        int mpot_id = flowerpot_ids[(int) pot_spinner.getSelectedItemId()-1];
                        Log.e("get_sensor_chart", "您选择的是：" + pot_adapter.getItem(arg2) +
                                "pot_id: " + mpot_id + "sensor_id: " + sensor_spinner.getSelectedItemId());
                        showProgress(true);
                        mAuthTask = new GetDatasTask(CurrentTimeStr, mpot_id, (int) sensor_spinner.getSelectedItemId());
                        mAuthTask.execute((Void) null);
                    }

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
        sensor_list.add("二氧化碳浓度");
        sensor_list.add("光照强度");
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
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
                /* 将所选mySpinner 的值带入myTextView 中*/

                Log.e("get_sensor_chart", "您选择的是：" + sensor_adapter.getItem(arg2) );

                    if (pot_spinner.getSelectedItemId() != 0 && sensor_spinner.getSelectedItemId() != 0) {
                        String CurrentTimeStr = chart_time.getText().toString();
                        int mpot_id = flowerpot_ids[(int) pot_spinner.getSelectedItemId()-1];
                        Log.e("get_sensor_chart", "您选择的是：" + pot_adapter.getItem(arg2) +
                                "pot_id: " + mpot_id + "sensor_id: " + sensor_spinner.getSelectedItemId());
                        showProgress(true);
                        mAuthTask = new GetDatasTask(CurrentTimeStr, mpot_id, (int) sensor_spinner.getSelectedItemId());
                        mAuthTask.execute((Void) null);
                    }

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
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mChartFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mChartFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mChartFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mChartFormView .setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /*后台访问网络获取数据的类*/
    public class GetDatasTask extends AsyncTask<Void, Void, Boolean> {
        private String mdate;
        private int mpot;
        private int msensor;
        //private double[] msensorDatas;
        //private int Datasnum;
        private float max = -9999;
        private float min = 9999;
        private int maxY;
        private int minY;
        private int left;
        private int right;
        GetDatasTask(String datetime,int pot_id,int sensor_id) {
            mdate = datetime;
            mpot = pot_id;
            msensor = sensor_id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String result;
            try {
                // Simulate network access.
                String data = "mdate=" + URLEncoder.encode(mdate, "utf-8") + "&flowerpot_id="+ URLEncoder.encode(mpot+"","utf-8")+ "&sensor_id="+ URLEncoder.encode(msensor+"","utf-8");
                //本地测试地址
                String url = "http://192.168.191.1:8000/get_sensorDatas/?"+ data;
                //String url = "http://flowerpot.applinzi.com/get_sensorDatas/?" + data;
                result = connection.URLGet_sensorDatas(url);
                JSONObject jsonstr = new JSONObject(result);
                left = jsonstr.getInt("mstarttime");
                right = jsonstr.getInt("mendtime");
                numberOfPoints = jsonstr.getInt("msensor_datas_num");
                JSONArray datas = jsonstr.getJSONArray("msensor_datas");
                //msensorDatas  = new double[Datasnum];
                for(int i = 0;i < datas.length();i++){
                    sensorDatasTab[i] = (float)datas.getDouble(i);
                    if(max < sensorDatasTab[i]){
                        max = sensorDatasTab[i];
                    }
                    if(min > sensorDatasTab[i]){
                        min = sensorDatasTab[i];
                    }
                    Log.i("NET_Get_flowerpots",sensorDatasTab[i]+"");
                }
            } catch (InterruptedException e) {
                return false;
            }catch (Exception e) {
                e.printStackTrace();

                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            maxY = (int) max + 1;
            minY = (int)min - 1;
            String AxisYName = "ERROR";
            if (success) {
                //TODO:获取数据成功后在图表中显示
                toggleLabels();
                switch (msensor){
                    case 1:
                        AxisYName = "温度";
                        break;
                    case 2:
                        AxisYName = "土壤湿度";
                        break;
                    case 3:
                        AxisYName = "二氧化碳";
                        break;
                    case 4:
                        AxisYName = "光照强度";
                        break;
                }
                generateData(AxisYName,left);
                // Disable viewport recalculations, see toggleCubic() method for more info.
                chart.setViewportCalculationEnabled(false);

                resetViewport(minY,maxY,left,right);
            } else {
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
