package com.apperture.idealink;
/**
 * Created by Pradeep on 02-12-2014.
 */
import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.zip.Inflater;


public class Home extends Activity implements SensorEventListener {

    SensorManager sm;
    Sensor rotation;

   public static ActionBar publicactionBar;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
   public   static CharSequence mTitle="";

    // Intent request codes
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
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    // private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for Bluetooth Command Service
    // private BluetoothCommandService mCommandService = null;
    static TextView state;
    Boolean isChecked=false;
    Boolean Gclicked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mTitle = getTitle();
        state=(TextView)findViewById(R.id.state);
        // Set up the drawer.


        Backend.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported

        if (Backend.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        rotation=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, rotation, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
        @Override
        public void onNavigationDrawerItemSelected(int position) {
            // update the main content by replacing fragments

            FragmentManager fragmentManager = getFragmentManager();
            if(position==1)
                fragmentManager.beginTransaction()
                        .replace(R.id.container, key_mouse.newInstance()).commit();
            else
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();

           // if(position==1)///if key and mouse
            //onCreateKeyMouse();
        }

        public void onSectionAttached(int number) {
            switch (number) {
                case 1:
                    mTitle = getString(R.string.title_section1);
                    break;
                case 2:
                    mTitle = getString(R.string.title_section2);
                    //onCreateKeyMouse();
                    break;
                case 3:
                    mTitle = getString(R.string.title_section3);
                    break;
            }
        }
    */
    public void restoreActionBar() {
        state=(TextView)findViewById(R.id.state);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        publicactionBar=actionBar;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.option_menu, menu);
            restoreActionBar();
            return true;
        }*/
        getMenuInflater().inflate(R.menu.option_menu, menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.scan:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    // public static class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //   private static final String ARG_SECTION_NUMBER = "section_number";
    // private static int sectionN=0;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
      /*  public static PlaceholderFragment newInstance(int sectionNumber) {
            Log.d("Placeholder","Sec No is"+Integer.toString(sectionNumber));
            sectionN=sectionNumber;
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView;
            switch(sectionN){
                case 2:  rootView = inflater.inflate(R.layout.fragment_key_mouse, container, false);
                                    ;break;
                default:rootView=inflater.inflate(R.layout.fragment_home, container, false);break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Home) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    protected void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupCommand() will then be called during onActivityResult
        if (!Backend.mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        // otherwise set up the command service
        else {
            if (Backend.mCommandService==null)
                setupCommand();
        }
    }
    */
    protected void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (Backend.mCommandService != null) {
            if (Backend.mCommandService.getState() == BluetoothCommandService.STATE_NONE) {
                Backend.mCommandService.start();
            }
        }
        displayState(Backend.append);
        CheckBox box;
        box =(CheckBox)findViewById(R.id.gesture);
        box.setChecked(isChecked);
        Log.d("res","resumed");
    }
    protected void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupCommand() will then be called during onActivityResult
        if (!Backend.mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        // otherwise set up the command service
        else {
            if (Backend.mCommandService==null)
                setupCommand();
        }
    }
    private void setupCommand() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        //  Backend.mCommandService = new BluetoothCommandService(Backend.getInstance(), Backend.mHandler);
        Backend.setupCommand();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Home", "Destroyed");
       // if (Backend.mCommandService != null)
         //   Backend.mCommandService.stop();
    }

    private void ensureDiscoverable() {
        if (Backend.mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    // The Handler that gets information back from the BluetoothChatService
   /* public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothCommandService.STATE_CONNECTED:
                         //MenuItem menu;
                           // menu.
                          //  MTitle.setText(R.string.title_connected_to);
                          //  MTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothCommandService.STATE_CONNECTING:
                            //MTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothCommandService.STATE_LISTEN:
                        case BluetoothCommandService.STATE_NONE:
                            //MTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
*/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Backend.onActivityResult(requestCode, resultCode, data);
       /* switch (requestCode) {
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
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }*/


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Backend.mCommandService.write("Nav!!U!!");
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Backend.mCommandService.write("Nav!!D!!");
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public static boolean displayState(String curState){

        try {
            state.setText(curState);
        }
        catch(Exception e){return false;}
        Log.d("Home","statedisplay true");
        return true;
    }


    public void ButtonClicked(View view) {

        switch (view.getId()) {
            case R.id.up:
                Backend.mCommandService.write("Nav!!U!!");
                return;

            case R.id.down:
                Backend.mCommandService.write("Nav!!D!!");
                return;

            case R.id.left:
                Backend.mCommandService.write("Nav!!L!!");
                return;

            case R.id.right:
                Backend.mCommandService.write("Nav!!R!!");
                return;

            default:
                Log.d("Home ButtonClicked", "Clicked");
        }

    }

    public void OnClick(View view)
    {Intent intent;
        switch(view.getId()){
            case R.id.key: intent = new Intent(this, Keyboard.class);
                    startActivity(intent);break;

            case R.id.Media: intent = new Intent(this, MediaVlc.class);
                        startActivity(intent);break;
        }
    }

    public void gestureSwitch(View view){
        Log.d("Gesture Switch called",Integer.toString(sm.getSensorList(20).toArray().length));



       // deviceSensors.get(1).getName()
        CheckBox box;
/*
        if (sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED) != null){
            Log.d("Gyro ","Present");
        }
        else {
            Log.d("Gyro ","NOT Present");
        }
*/
        box=(CheckBox)view;
        if(box.isChecked()){
            Log.d("box"," checked");
           // sm.registerListener(this, rotation, SensorManager.SENSOR_DELAY_NORMAL);
            isChecked=true;
        }
        else
        { Log.d("box"," unchecked");
            isChecked=false;
           // sm.unregisterListener(this, rotation);
        }
    }

    /**
     * Called when sensor values have changed.
     * <p>See {@link android.hardware.SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link android.hardware.SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link android.hardware.SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        CheckBox box;
        box =(CheckBox)findViewById(R.id.gesture);
        box.setChecked(isChecked);
          double base=7.5;
       // Log.d("X= ",Float.toString(event.values[0]));
        //Log.d("y= ",Float.toString(event.values[1]));
        //Log.d("z= ",Float.toString(event.values[2]));
      if (isChecked)
        if(event.values[0]>base && !Gclicked){
            //
            Backend.mCommandService.write("Ges!!L!!");
            Log.d("X","Left"+Boolean.toString(isChecked));
            Gclicked=true;

        }
       else if(event.values[0]<-1*base && !Gclicked){
            //
            Backend.mCommandService.write("Ges!!R!!");
            Log.d("X","Right");
            Gclicked=true;

        }
       else if(event.values[1]>base && !Gclicked){
            //
            Backend.mCommandService.write("Ges!!D!!");
            Log.d("Y","down");

            Gclicked=true;

        }
       else if(event.values[1]<-1*base+1.5 && !Gclicked){
            //
            Backend.mCommandService.write("Ges!!U!!");
            Log.d("Y","up");
            Gclicked=true;
        }
        else if(Gclicked){
            Backend.mCommandService.write("Ges!!r!!");
            Log.d("sensr","release");
            Gclicked=false;
        }


    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link android.hardware.SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
onBackPressed();
    }
    @Override
    public void onBackPressed(){
      //  System.runFinalizersOnExit(true);
       // System.exit(0);
    }
}




