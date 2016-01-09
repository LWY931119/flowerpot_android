package com.btflowerpot.btflowerpot.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.btflowerpot.btflowerpot.DataBaseHelper.BTDataBaseHelper;
import com.btflowerpot.btflowerpot.R;
import com.gc.materialdesign.views.ScrollView;

/**
 * Created by Administrator on 2016/1/5.
 */
public class get_sensor_text extends Fragment {
    private Button select;
    private Button addbtn;
    private TextView all;
    private static final String TAG = "BluetoothChat";
    private BTDataBaseHelper dbHelper;
    private final String success_result = "t";
    private final String fail_result = "f";
    private ScrollView ss;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.get_sensor_text, container, false);
        all = (TextView) v.findViewById(R.id.all);
        select = (Button)v.findViewById(R.id.btn1);
        addbtn  = (Button)v.findViewById(R.id.btn2);
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
                //p.show();

            }
        });

        return v;
    }
    @Override
    public void onCreate(Bundle savedTnstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedTnstanceState);
    }

}
