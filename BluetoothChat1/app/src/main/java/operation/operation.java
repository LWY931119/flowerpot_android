package operation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import network.conn;

/**
 * Created by Administrator on 2015/11/21.
 */
public class operation {
    public String[] select_all()
    {
        String[] datas = new String[]{};
        String result = null;
        conn connNet=new conn();
        List<NameValuePair> params=new ArrayList<NameValuePair>();

        try {
            HttpEntity entity=new UrlEncodedFormEntity(params,HTTP.UTF_8);
            HttpPost httpPost=connNet.gethttPost("GetDatas");
            // System.out.println(httpPost.toString());
            Log.i("lwy",httpPost.toString());
            httpPost.setEntity(entity);
            HttpClient client=new DefaultHttpClient();
            HttpResponse httpResponse=client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
            {
                result=EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                datas = result.split("\\*");
                for (int i = 0; i < datas.length; i++) {
                    System.out.println(datas[i]);
                }
            }
            else
            {
                result="登录失败";
            }
        } catch (UnsupportedEncodingException e) {
            Log.i("error","error1");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.i("error","error2");
            e.printStackTrace();
        } catch (ParseException e) {
            Log.i("error","error3");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("error","error4");
            e.printStackTrace();
        }
        return datas;
    }
    public String addData(String  all_data)
    {
        if(all_data.length() > 0) {
            String result = null;
//            String jsonString = new String();
//            for (int i = 0; i < all_data.size(); i++) {
//                jsonString += all_data.get(i) + '+';
//            }
//            System.out.println("@@@jsonString " + jsonString);
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            NameValuePair nvp = new BasicNameValuePair("jsonstring", all_data);
            list.add(nvp);
            conn connNet = new conn();
            HttpPost httpPost = connNet.gethttPost("AddData");
            try {
                HttpEntity entity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
                //此句必须加上否则传到客户端的中文将是乱码
                httpPost.setEntity(entity);
                HttpClient client = new DefaultHttpClient();
                HttpResponse httpResponse = client.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                    System.out.println("resu" + result);
                } else {
                    result = "插入数据库失败";
                    System.out.println("result---->" + result);
                }
            } catch (UnsupportedEncodingException e) {
                System.out.println("catch UnsupportedEncodingException");
                e.printStackTrace();
                return "UnsupportedEncodingException";
            } catch (ClientProtocolException e) {
                System.out.println("catch ClientProtocolException");
                e.printStackTrace();
                return "ClientProtocolException";
            }catch (SocketException e){
                System.out.println("catch SocketException");
                e.printStackTrace();
                return "连接服务器失败";
            }catch (SocketTimeoutException e){
                System.out.println("catch SocketTimeoutException");
                e.printStackTrace();
                return "SocketTimeoutException";
            } catch (ParseException e) {
                System.out.println("catch ParseException");
                e.printStackTrace();
                return "ParseException";
            } catch (IOException e) {
                System.out.println("catch IOException");
                e.printStackTrace();
                return "IOException";
            }
            return result;
        }
        else{
            return "No Datas,Can't insert into database";
        }
    }
}
