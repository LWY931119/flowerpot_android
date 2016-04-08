package com.btflowerpot.btflowerpot.netWork;

import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
//import org.apache.http.*;
/**
 * Created by Administrator on 2016/1/7.
 */
public class connection {
    private static final String URLVAR="http://192.168.191.1:8080/BT/";
    //将路径定义为一个常量，修改的时候也好更改
    //通过url获取网络连接  connection
    public HttpURLConnection getConn(String urlpath)
    {
        String finalurl=URLVAR+urlpath;
        HttpURLConnection connection = null;
        try {
            URL url=new URL(finalurl);
            connection=(HttpURLConnection) url.openConnection();
            connection.setDoInput(true);  //允许输入流
            connection.setDoOutput(true); //允许输出流
            connection.setUseCaches(false);  //不允许使用缓存
            connection.setRequestMethod("POST");  //请求方式
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return connection;

    }


    public  static int UPLoadSensorDatas(String datas) throws Exception{
        String path = "http://192.168.191.1:8000/upload_sensortext/";
        //String path = "http://flowerpot.applinzi.com/upload_sensortext/";
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(60000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream (
                conn.getOutputStream ());
        wr.writeBytes(datas);
        wr.flush();
        wr.close();
        // 判断请求Url是否成功
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }

        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        // 为输出创建BufferedReader
        BufferedReader buffer = new BufferedReader(in);
        String inputLine = null;
        String response_result = new String();
        //使用循环来读取获得的数据
        while (((inputLine = buffer.readLine()) != null))
        {
            //我们在每一行后面加上一个"\n"来换行
            response_result += inputLine + "\n";

        }
        in.close();
        if(response_result.equals(LOGINSUCCESS)){
            return 1;
        }
        else if(response_result.equals(LOGINFAILED)){
            return 2;
        }
        else if(response_result.equals(REGESTSUCCESS)){
            return 3;
        }
        else if(response_result.equals(REGESTFAILED)){
            return 4;
        }
        Log.e("login_regest", "Unknown error");
        return 0;
    }
    private static String LOGINSUCCESS = new String("login success"+ "\n");
    private static String LOGINFAILED = new String("login failed"+ "\n");
    private static String REGESTSUCCESS = new String("regest success"+ "\n");
    private static String REGESTFAILED = new String("regest failed"+ "\n");
    public static int Login_Regest(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(5000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("GET");
        // 判断请求Url是否成功
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        //Log.e("2222", conn.getOutputStream().toString());
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        // 为输出创建BufferedReader
        BufferedReader buffer = new BufferedReader(in);
        String inputLine = null;
        String response_result = new String();
        //使用循环来读取获得的数据
        while (((inputLine = buffer.readLine()) != null))
        {
            //我们在每一行后面加上一个"\n"来换行
            response_result += inputLine + "\n";

        }
        in.close();
        if(response_result.equals(LOGINSUCCESS)){
            return 1;
        }
        else if(response_result.equals(LOGINFAILED)){
            return 2;
        }
        else if(response_result.equals(REGESTSUCCESS)){
            return 3;
        }
        else if(response_result.equals(REGESTFAILED)){
            return 4;
        }
        Log.e("login_regest", "Unknown error");
        return 0;
    }

    public static int[] URLGet_flowerpot(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(5000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("GET");
        // 判断请求Url是否成功
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        //Log.e("2222", conn.getOutputStream().toString());
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        // 为输出创建BufferedReader
        BufferedReader buffer = new BufferedReader(in);
        String inputLine = null;
        String response_result = new String();
        //使用循环来读取获得的数据
        while (((inputLine = buffer.readLine()) != null))
        {
            response_result += inputLine;
        }
        in.close();
        Log.i("NET_Get_flowerpots", response_result);
        JSONObject jsonstr = new JSONObject(response_result);
        int pot_num = jsonstr.getInt("pot_num");
        JSONArray flowerpots = jsonstr.getJSONArray("flowerpot_id");
        int []flowerpot_id  = new int[pot_num];
        for(int i = 0;i < flowerpots.length();i++){
            flowerpot_id[i] = flowerpots.getInt(i);
            Log.i("NET_Get_flowerpots",flowerpot_id[i]+"");
        }
        return  flowerpot_id;
    }
    public static String URLGet_sensorDatas(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(5000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("GET");
        // 判断请求Url是否成功
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        //Log.e("2222", conn.getOutputStream().toString());
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        // 为输出创建BufferedReader
        BufferedReader buffer = new BufferedReader(in);
        String inputLine = null;
        String response_result = new String();
        //使用循环来读取获得的数据
        while (((inputLine = buffer.readLine()) != null))
        {
            response_result += inputLine;
        }
        in.close();
        Log.i("NET_Get_sensorDatas", response_result);
        return response_result;
    }
}
