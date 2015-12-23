package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import network.conn;
import operation.operation;
import dbHelper.BTdbHelper;
/**
 * Created by Administrator on 2015/11/17.
 */
public class textActivity extends Activity{
    private Button select;
    private Button addbtn;
    private TextView all;
    private static final String TAG = "BluetoothChat";
    private BTdbHelper dbHelper;
    private final String success_result = "t";
    private final String fail_result = "f";
    public void onCreate(Bundle savedTnstanceState)
    {
        super.onCreate(savedTnstanceState);
        setContentView(R.layout.textpage);
        all = (TextView) findViewById(R.id.all);
        all.setMovementMethod(ScrollingMovementMethod.getInstance());
        Intent intent = getIntent();
        //all_data������¼���ϸ�ҳ�洫������Ϣ
        final ArrayList<String> all_data = (ArrayList<String>) intent.getSerializableExtra("result");
        //从数据库读出值来
        dbHelper = new BTdbHelper(this, "btDB.db3", 1);
        Cursor cur = dbHelper.getReadableDatabase().rawQuery("select * from data_table",null);
        int ij = 0;
        all.append("Data From local Database\n");
        while(cur.moveToNext())
        {
            all.append(cur.getInt(0)+" :"+cur.getString(1)+" "+ cur.getString(2)+"\n");
            ij++;
        }
//        all.append("Data From BT\n");
//        for(int i=0;i<all_data.size();i++)
//        {
//            Log.i(TAG, all_data.get(i));
//            all.append( all_data.get(i)+'\n');
//        }
        select = (Button)findViewById(R.id.btn1);
        addbtn  = (Button)findViewById(R.id.btn2);

        select.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //p.show();
                new Thread(new Runnable() {

                    public void run() {
                        operation operaton = new operation();
                        String[] result = operaton.select_all();
                        //Log.i("test", result);
                        Message msg = new Message();
                        msg.obj = result;
                        handler.sendMessage(msg);
                        //p.dismiss();
                    }
                }).start();
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //p.show();
                new Thread(new Runnable() {

                    public void run() {
                        operation operaton=new operation();
                        Cursor cur = dbHelper.getReadableDatabase().rawQuery("select * from data_table",null);
                        int ij = 0;
                        while(cur.moveToNext())
                        {
                            String add_data = cur.getString(1)/*+"+"+ cur.getString(2)*/;

                            String result = operaton.addData(add_data);
                            if(result.equals(success_result))
                            {
                                dbHelper.getReadableDatabase().delete("data_table", "id = ?", new String[]{cur.getString(0)});
                            }
                            Message msg=new Message();
                            System.out.println("result---->"+result);
                            msg.obj=result;
                            handler1.sendMessage(msg);
                            ij++;
                        }
                    }
                }).start();
            }
        });
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String[] result=(String[]) msg.obj;
            //p.dismiss();
            all.append("Data From DataBase\n");
            for (int i = 0; i < result.length; i++) {
                System.out.println(result[i]);
                all.append(result[i]+'\n');
            }

            super.handleMessage(msg);
        }
    };
    Handler handler1=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            //dialog.dismiss();
            String msgobj=msg.obj.toString();
            if(msgobj.equals("t"))
            {
                Toast.makeText(textActivity.this, "Sucess", 0).show();

            }
            else {
                Toast.makeText(textActivity.this, msg.obj.toString(), 0).show();
            }
            super.handleMessage(msg);
        }
    };
}
