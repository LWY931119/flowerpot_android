package dbHelper;

/**
 * Created by Administrator on 2015/12/6.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BTdbHelper extends SQLiteOpenHelper {
    private static final String TAG = "BTSQLite";
    public BTdbHelper(Context context, String name,int version) {
        super(context, name,(CursorFactory)null, version);
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

