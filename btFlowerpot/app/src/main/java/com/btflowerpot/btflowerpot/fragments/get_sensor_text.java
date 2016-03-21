package com.btflowerpot.btflowerpot.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.btflowerpot.btflowerpot.DataBaseHelper.BTDataBaseHelper;
import com.btflowerpot.btflowerpot.R;
import com.btflowerpot.btflowerpot.netWork.connection;
import com.gc.materialdesign.views.ScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/1/5.
 */
public class get_sensor_text extends Fragment {
    private UPloadTask mAuthTask = null;
    private Button select;
    private Button addbtn;
    private TextView all;
    private static final String TAG = "BluetoothChat";
    private BTDataBaseHelper dbHelper;
    private final String success_result = "t";
    private final String fail_result = "f";
    private ScrollView ss;
    private View mSensorDatasFormView;
    private View mProgressView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.get_sensor_text, container, false);
        all = (TextView) v.findViewById(R.id.all);
        select = (Button)v.findViewById(R.id.btn1);
        addbtn  = (Button)v.findViewById(R.id.btn2);
        mSensorDatasFormView = v.findViewById(R.id.sensor_datas_form);
        mProgressView = v.findViewById(R.id.sensor_datas_progress);
        dbHelper = new BTDataBaseHelper(this.getContext(), "btDB.db3", 1);
        Cursor cur = dbHelper.getReadableDatabase().rawQuery("select * from data_table",null);
        int ij = 0;
        all.append("Data From local Database\n");
        while(cur.moveToNext())
        {
            String dataString = cur.getString(1).replace("text:","");
            all.append(cur.getInt(0)+" :"+dataString+" "+ cur.getString(2)+"\n");
            ij++;
        }
        select.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //p.show();
                new Thread(new Runnable() {

                    public void run() {
                /*        operation operaton = new operation();
                        String[] result = operaton.select_all();
                        //Log.i("test", result);
                        Message msg = new Message();
                        msg.obj = result;
                        handler.sendMessage(msg);
                        //p.dismiss();*/
                    }
                }).start();
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showProgress(true);
                mAuthTask = new UPloadTask();
                mAuthTask.execute((Void) null);

            }
        });

        return v;
    }
    @Override
    public void onCreate(Bundle savedTnstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedTnstanceState);
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

            mSensorDatasFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSensorDatasFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSensorDatasFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSensorDatasFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UPloadTask extends AsyncTask<Void, Void, Boolean> {


        UPloadTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            int result;
            String[] sensor_datas = new String[]{};
            String tem = "Tem";
            String moi = "Moi";
            String lig = "Lig";
            String co2 = "SEN0159";
            try {
                JSONObject connection_body = new JSONObject();
                JSONArray datas = new JSONArray();
                Cursor cur = dbHelper.getReadableDatabase().rawQuery("select * from data_table",null);
                int ij = 0;
                while(cur.moveToNext())
                {
                    ij++;
                    if(ij == 70)break;
                    String dataString = cur.getString(1).replace("text:","");
                    sensor_datas = dataString.split("\\+");
                    String[] tags = new String[]{};
                    tags = sensor_datas[0].split(":");
                    if(tags[0].equals(tem))
                    {
                        System.out.println("Tem "+Double.valueOf(tags[1]) + "Time: "+sensor_datas[1]);
                        double data=Double.parseDouble(tags[1]);//将收到的字符串解析成double
                        JSONObject dataObj = new JSONObject();
                        dataObj.put("flowerpot_id",1);
                        dataObj.put("sensor_id",1);
                        dataObj.put("unit_id",1);
                        dataObj.put("sensorchannel_id",1);
                        dataObj.put("data",data);
                        dataObj.put("time",sensor_datas[1]);
                        datas.put(dataObj);
                    }
                    else if(tags[0].equals(moi))
                    {
                        System.out.println("Moi "+Double.valueOf(tags[1]));
                        double data=Double.parseDouble(tags[1]);//将收到的字符串解析成double
                        JSONObject dataObj = new JSONObject();
                        dataObj.put("flowerpot_id",1);
                        dataObj.put("sensor_id",2);
                        dataObj.put("unit_id",2);
                        dataObj.put("sensorchannel_id",2);
                        dataObj.put("data",data);
                        dataObj.put("time",sensor_datas[1]);
                        datas.put(dataObj);

                    }
                    else if(tags[0].equals(lig))
                    {
                        System.out.println("Lig "+Double.valueOf(tags[1]));
                        double data=Double.parseDouble(tags[1]);//将收到的字符串解析成double
                        JSONObject dataObj = new JSONObject();
                        dataObj.put("flowerpot_id",1);
                        dataObj.put("sensor_id",4);
                        dataObj.put("unit_id",4);
                        dataObj.put("sensorchannel_id",4);
                        dataObj.put("data",data);
                        dataObj.put("time",sensor_datas[1]);
                        datas.put(dataObj);

                    }
                    else if(tags[0].equals(co2))
                    {
                        System.out.println("Co2 " + Double.valueOf(tags[2]));
                        double data=Double.parseDouble(tags[2]);//将收到的字符串解析成double
                        JSONObject dataObj = new JSONObject();
                        dataObj.put("flowerpot_id",1);
                        dataObj.put("sensor_id",3);
                        dataObj.put("unit_id",3);
                        dataObj.put("sensorchannel_id",3);
                        dataObj.put("data",data);
                        dataObj.put("time",sensor_datas[1]);
                        datas.put(dataObj);
                    }
                    else{
                        //数据库中有一条数据格式错误
                        break;
                    }
                }
                connection_body.put("count",ij);
                connection_body.put("data",datas);
                Log.e("lllllll", connection_body.toString());
                // Simulate network access
                String url = "http://192.168.191.1:8000/upload_sensortext/";
                result = connection.UPLoadSensorDatas(url,connection_body.toString());

                //Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            switch (result){
                case 0:
                    Log.e("login_regest", "Unknown error");
                    return false;
                case 1:Log.e("login_regest","login success");
                    return true;
                case 2:Log.e("login_regest","login failed");
                    return false;
                case 3:Log.e("login_regest","regest success");
                    return true;
                case 4:
                    Log.e("login_regest","regest error");
                    return true;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
