package com.btflowerpot.btflowerpot.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.btflowerpot.btflowerpot.BluetoothChatService;
import com.btflowerpot.btflowerpot.DataBaseHelper.BTDataBaseHelper;
import com.btflowerpot.btflowerpot.DeviceListActivity;
import com.btflowerpot.btflowerpot.MainActivity;
import com.btflowerpot.btflowerpot.R;
import com.gc.materialdesign.views.Switch;

/**
 * Created by Administrator on 2016/1/5.
 */
public class get_current_sensor extends Fragment {

    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    //DataBase
    private BTDataBaseHelper dbHelper;
    //UI上的零件
    private static TextView mTitle;
    private TableRow settime_row;
    private TableRow gettime_row;
    private TableRow Tem_row ;
    private TableRow Moi_row ;
    private TableRow Co2_row ;
    private TableRow Lig_row ;

    private Button settime_btn;
    private Button gettime_btn;
    private Button Tem_btn;
    private Button Moi_btn;
    private Button Co2_btn;
    private Button Lig_btn;
    private Button gettext_btn;

    private TextView Tem_text;
    private TextView Moi_text;
    private TextView Co2_text;
    private TextView Lig_text;

    //传感器数据的标签
    private static String tem = "Tem";
    private static String moi = "Moi";
    private static String lig = "Lig";
    private static String co2 = "SEN0159";
    private static String text = "text";
    public Button t;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.get_current_sensor, container, false);

        Tem_btn = (Button)v.findViewById(R.id.Tem_btn);
        Moi_btn = (Button)v.findViewById(R.id.Moi_btn);
        Co2_btn = (Button)v.findViewById(R.id.Co2_btn);
        Lig_btn = (Button)v.findViewById(R.id.Lig_btn);
        gettext_btn = (Button)v.findViewById(R.id.gettext_btn);
        settime_btn = (Button)v.findViewById(R.id.settime_btn);
        gettime_btn = (Button)v.findViewById(R.id.gettime_btn);

        Tem_text = (TextView)v.findViewById(R.id.temperature);
        Moi_text = (TextView)v.findViewById(R.id.Moisture);
        Co2_text = (TextView)v.findViewById(R.id.Co2);
        Lig_text = (TextView)v.findViewById(R.id.Light);
        Switch sss  = (Switch)v.findViewById(R.id.switchView);
        sss.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch view, boolean check) {
                if(check){
                    //是已连接状态才改变界面
                    if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                        Invisible_all_row();
                        sendMessage("changemode");
                    }
                }
                else{
                    if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                        Visible_all_row();
                        sendMessage("changemode");
                    }
                }
            }
        });
        return v;
    }
    void Invisible_all_row(){
        //TODO:将所有的普通行隐藏，设置行显示
    }
    void Visible_all_row(){
        //TODO:将所有的普通行显示，设置行隐藏
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.dbHelper = new BTDataBaseHelper(this.getContext(), "btDB.db3", 1);
        mTitle = (TextView) getActivity().findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) getActivity().findViewById(R.id.title_right_text);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }
    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    public void onDetach(){
        super.onDetach();
        Log.e(TAG, "--- ON Detach ---");
    }
    private void setupChat() {
        Log.d(TAG, "setupChat()");
        // Initialize the BluetoothChatService to perform bluetooth connections
        Tem_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage("f");
            }
        });
        gettext_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage("b");
            }
        });
        Moi_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage("e");
            }
        });
        Lig_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage("d");
            }
        });
        Co2_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage("c");
            }
        });
        settime_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage("settime");
            }
        });
        gettime_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage("gettime");
            }
        });
        mChatService = new BluetoothChatService(this.getContext(), mHandler);

    }
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

        }
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.scan:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                Log.e("ss", "discoverable()");
                return true;
        }
        return false;
    }
    private  final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            mTitle.setText(R.string.title_connected_to);
                            mTitle.append(mConnectedDeviceName);

                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //Arduino发来的数据格式是Moi:10+2015-12-12 12:12:12，这句话会直接插到本地数据库。
                    //然后读出这句话直接发到服务器，有服务器解析，然后再插入服务器的数据库
                    String[] datas = new String[]{};
                    datas = readMessage.split("\\+");
                    String[] tags = new String[]{};
                    tags = datas[0].split(":");
                    if(tags[0].equals(tem) ) {
                        Tem_text.setText(tags[1]);
                    }
                    else if(tags[0].equals(moi)){
                        Moi_text.setText(tags[1]);
                    }
                    else if(tags[0].equals(lig)){
                        Lig_text.setText(tags[1]);
                    }
                    else if(tags[0].equals(co2)){
                        Co2_text.setText(tags[2]);
                    }
                    else if(tags[0].equals(text)){
                        ContentValues cv = new ContentValues();
                        cv.put("data_string", readMessage);
                        dbHelper.getWritableDatabase().insert("data_table", null, cv);
                    }
                    else
                    {
                        Toast.makeText(getActivity(),readMessage, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getActivity(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Log.i(TAG, msg.getData().getString(TOAST));
                    try {
                        Toast.makeText(getActivity(), msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                        Log.i(TAG, msg.getData().getString(TOAST));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };
}
