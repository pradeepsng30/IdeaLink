package com.apperture.idealink;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Pradeep on 07-12-2014.
 */
public class Backend extends android.app.Application {
    private static Backend instance = new Backend();

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothCommandService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Name of the connected device
    private static String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    public static BluetoothAdapter mBluetoothAdapter = null;
    // Member object for Bluetooth Command Service
    public static BluetoothCommandService mCommandService = null;

    public static String append="";





    public static Context getInstance()
    {
        if (instance == null) instance = new Backend();

        return instance;
    }


    public void onCreate() {
        instance = this;
        super.onCreate();


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            //finish();
            return;
        }
    }

    public static void restoreState() {
       /* try {
            ActionBar actionBar = Home.publicactionBar;

            // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            // actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(Home.mTitle + "\t" + append);
        }
        catch(Exception e){
            e.printStackTrace();
        }*/
        Log.d("Backend","statedisplay");
        Home.displayState(append);
        MediaVlc.displayState(append);
        Keyboard.displayState(append);

    }
    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothCommandService.STATE_CONNECTED:
                            append="Linked:"+mConnectedDeviceName;
                            restoreState();
                            //actionBar.setTitle(mTitle);
                            //MenuItem menu;
                            // menu.
                            //  MTitle.setText(R.string.title_connected_to);
                            //  MTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothCommandService.STATE_CONNECTING:
                            //MTitle.setText(R.string.title_connecting);
                            append="Linking..";
                            restoreState();
                            break;
                        case BluetoothCommandService.STATE_LISTEN:
                        case BluetoothCommandService.STATE_NONE:
                            //MTitle.setText(R.string.title_not_connected);
                            append="UnLinked";
                            restoreState();
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                   // Toast.makeText(getInstance(), "Connected to "
                    //        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                   // Toast.makeText(getInstance(), msg.getData().getString(TOAST),
                   //        Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = Backend.mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    Backend.mCommandService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupCommand();
                } else {
                    // User did not enable Bluetooth or an error occured
                   // Toast.makeText(getInstance(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();

                }
        }
    }
    public static void setupCommand() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        Backend.mCommandService = new BluetoothCommandService(Backend.getInstance(), Backend.mHandler);

    }


}
