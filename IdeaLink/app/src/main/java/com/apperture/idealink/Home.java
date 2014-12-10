package com.apperture.idealink;
/**
 * Created by Pradeep on 08-12-2014.
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Home extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, key_mouse.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
   ClipData.Item MTitle;
    int lastXpos = 0;
    int lastYpos = 0;
    boolean keyboard = false;
    public static int mouse_sensitivity = 1;
    String delim = new String("!!");
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));



        Backend.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported

        if (Backend.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

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

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.option_menu, menu);
            restoreActionBar();
            return true;
        }
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static int sectionN=0;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
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
        if (Backend.mCommandService != null)
            Backend.mCommandService.stop();
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
        Backend.onActivityResult(requestCode,resultCode,data);
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
            Backend.mCommandService.write(BluetoothCommandService.VOL_UP);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            Backend.mCommandService.write(BluetoothCommandService.VOL_DOWN);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
/*
    public void ButtonClicked (View view){

        switch (view.getId()) {
            case R.id.up:Backend.mCommandService.write(BluetoothCommandService.UP);return;

            case R.id.down:Backend.mCommandService.write(BluetoothCommandService.DOWN);return;

            case R.id.left:Backend.mCommandService.write(BluetoothCommandService.LEFT);return;

            case R.id.right:Backend.mCommandService.write("Right");return;

            default:Log.d("Home ButtonClicked","Clicked");
        }

    }
*/
////////////////////////////////Key and Mouse////////////////////////////////////////////////

/*
    public void  onCreateKeyMouse(){
        Log.d("OnCreate","Key and Mouse");
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        Button left = (Button) findViewById(R.id.LeftClickButton);
        Button right =  (Button) findViewById(R.id.RightClickButton);

        left.setWidth(width/2);
        right.setWidth(width/2);

        View touchView = (View) findViewById(R.id.TouchPad);
        touchView.setOnTouchListener(this);

        EditText editText = (EditText) findViewById(R.id.KeyBoard);
        editText.setOnKeyListener(this);

        editText.addTextChangedListener(new TextWatcher(){
            public void  afterTextChanged (Editable s){
                Log.d("seachScreen", ""+s);
                s.clear();
            }
            public void  beforeTextChanged  (CharSequence s, int start, int count, int after){
                Log.d("seachScreen", "beforeTextChanged");
            }
            public void  onTextChanged  (CharSequence s, int start, int before, int count) {


                try{
                    char c = s.charAt(start);
                    Backend.mCommandService.write("KEY" + delim + c);
                }
                catch(Exception e){}
            }
        });

        onStartKeyMouse();
    }
*/
    public static void  onStartKeyMouse()
    {Backend.mCommandService.write("Mouse Sensitivity!!"+Integer.toString(mouse_sensitivity));
       // sendToAppDel(new String("Mouse Sensitivity!!"+appDel.mouse_sensitivity));
    }


    /**
     * Called when a hardware key is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     * <p>Key presses in software keyboards will generally NOT trigger this method,
     * although some may elect to do so in some situations. Do not assume a
     * software input method has to be key-based; even if it is, it may use key presses
     * in a different way than you expect, so there is no way to reliably catch soft
     * input key presses.
     *
     * @param v       The view the key has been dispatched to.
     * @param keyCode The code for the physical key that was pressed
     * @param event   The KeyEvent object containing full information about
     *                the event.
     * @return True if the listener has consumed the event, false otherwise.
     */

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d("Key", ""+event.getKeyCode());


        Backend.mCommandService.write("S_KEY"+delim+event.getKeyCode());
        return false;
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */

    public boolean onTouch(View v, MotionEvent event) {
        mousePadHandler(event);
        Log.d("Touch",event.toString());
        return true;

    }

    private void mousePadHandler(MotionEvent event) {
        StringBuilder sb = new StringBuilder();

        int action = event.getAction();
        int touchCount = event.getPointerCount();

        // if a single touch
        // send movement based on action
        if(touchCount == 1){
            switch(action){
                case 0: sb.append("DOWN"+delim);
                    sb.append((int)event.getX()+delim);
                    sb.append((int)event.getY()+delim);
                    break;

                case 1: sb.append("UP"+delim);
                    sb.append(event.getDownTime()+delim);
                    sb.append(event.getEventTime());
                    break;

                case 2: sb.append("MOVE"+delim);
                    sb.append((int)event.getX()+delim);
                    sb.append((int)event.getY());
                    break;

                default: break;
            }
        }

        // if two touches
        // send scroll message
        // based off MAC osx multi touch
        // scrolls up and down
        else if(touchCount == 2){
            sb.append("SCROLL"+delim);
            if(action == 2){
                sb.append("MOVE"+delim);
                sb.append((int)event.getX()+delim);
                sb.append((int)event.getY());
            }
            else
                sb.append("DOWN");
        }

        Backend.mCommandService.write(sb.toString());
    }

    public void LeftButtonClickHandler(View v){
        Log.d("eloo", "CLICKED");
        Backend.mCommandService.write("CLICK" + delim + "LEFT");
    }

    public void RightButtonClickHandler(View v){
        Backend.mCommandService.write("CLICK" + delim + "RIGHT");
    }

    // Show and hide Keyboard by setting the
    // focus on a hidden text field
    public void keyClickHandler(View v){
        EditText editText = (EditText) findViewById(R.id.KeyBoard);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(keyboard){
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            keyboard = false;
        }
        else{
            mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            keyboard = true;
        }

        Log.d("SET", "Foucs");
    }
}



