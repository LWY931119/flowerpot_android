package com.btflowerpot.btflowerpot.DataBaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/1/5.
 */
public class BTDataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "BTSQLite";
    public BTDataBaseHelper(Context context, String name, int version) {
        super(context, name,(SQLiteDatabase.CursorFactory)null, version);
    }

    // 当第一次创建数据库的时候，调用该方法
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table data_table(id INTEGER primary key AUTOINCREMENT,data_string TEXT,timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
//输出创建数据库的日志信息
        Log.i(TAG, "create Database------------->");
//execSQL函数用于执行SQL语句
        db.execSQL(sql);
    }

    //当更新数据库的时候执行该方法
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//输出更新数据库的日志信息
        Log.i(TAG, "update Database------------->");
    }
}


