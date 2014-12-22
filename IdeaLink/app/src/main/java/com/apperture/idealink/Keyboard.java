package com.apperture.idealink;
/**
 * Created by Pradeep on 08-12-2014.
 */
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Keyboard extends Activity implements View.OnTouchListener, View.OnKeyListener {


    int lastXpos = 0;
    int lastYpos = 0;
    boolean keyboard = false;
    Thread checking;
    static TextView state;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
   // private BluetoothCommandService mCommandService = null;
    String delim = new String("!!");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        state=(TextView)findViewById(R.id.state);
        Log.d("oncreate","Keyboard");
        Backend.restoreState();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        Button left = (Button) findViewById(R.id.LeftClickButton);
        Button right =  (Button) findViewById(R.id.RightClickButton);

        left.setWidth(width/2);
        right.setWidth(width/2);

        View touchView = (View) findViewById(R.id.TouchPad);
        touchView.setOnTouchListener(this);

        if (Backend.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        EditText editText = (EditText) findViewById(R.id.KeyBoard);
        editText.setOnKeyListener(this);
        editText.addTextChangedListener(new TextWatcher(){
            public void  afterTextChanged (Editable s){
                Log.d("seachScreen", "" + s);
                s.clear();
            }
            public void  beforeTextChanged  (CharSequence s, int start, int count, int after){
                Log.d("seachScreen", "beforeTextChanged");
            }
            public void  onTextChanged  (CharSequence s, int start, int before, int count) {
               // AppDelegate appDel = ((AppDelegate)getApplicationContext());

                try{
                    char c = s.charAt(start);
                   // appDel.sendMessage("KEY"+delim+c);
                    Backend.mCommandService.write("Key"+delim+c);
                }
                catch(Exception e){}
            }
        });
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
       // Backend.mCommandService = new BluetoothCommandService(this, mHandler);
    Backend.setupCommand();
    }


    public static boolean displayState(String curState){

        try {
            state.setText(curState);
        }
        catch(Exception e){return false;}
        Log.d("Key","statedisplay true");
        return true;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Backend.onActivityResult(requestCode,resultCode,data);
        /*
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
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }*/
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
        displayState(Backend.append);
    }


/*
    public void onStart(){
        super.onStart();

        AppDelegate appDel = ((AppDelegate)getApplicationContext());
        sendToAppDel(new String("Mouse Sensitivity!!"+appDel.mouse_sensitivity));

        new Thread(new Runnable(){
            AppDelegate appDel = ((AppDelegate)getApplicationContext());
            public void run(){
                while(appDel.connected){
                    try{Thread.sleep(1000);}
                    catch(Exception e){};
                    if(!appDel.connected){
                        finish();
                    }
                }
            }
        }).start();
    }
*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
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

    private void ensureDiscoverable() {
        if (Backend.mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
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
     * @param //keyCode The code for the physical key that was pressed
     * @param event   The KeyEvent object containing full information about
     *                the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onKey(View v, int c, KeyEvent event){
        Log.d("ello", ""+event.getKeyCode());
        //AppDelegate appDel = ((AppDelegate)getApplicationContext());

        //appDel.sendMessage("S_KEY"+delim+event.getKeyCode());
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
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mousePadHandler(event);
        return true;
    }

    private void sendMsg(String message){
       /* AppDelegate appDel = ((AppDelegate)getApplicationContext());
        if(appDel.connected){
            appDel.sendMessage(message);
        }
        else{
            finish();
        }*/
        try {
            Backend.mCommandService.write(message);
        }

        catch (Exception e){
            Log.e("Error string msg",e.toString());
        }
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
        Log.d("mouse", "L_CLICKED");
        Backend.mCommandService.write("CLICK" + delim + "LEFT");
    }

    public void RightButtonClickHandler(View v){
        Log.d("mouse", "R_CLICKED");
        Backend.mCommandService.write("CLICK" + delim + "RIGHT");
    }


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

    public void OnClick(View view)
    {Intent intent;
        switch(view.getId()){
            case R.id.Home: intent = new Intent(this, Home.class);
                startActivity(intent);break;
            case R.id.Media: intent = new Intent(this, MediaVlc.class);
                startActivity(intent);break;
        }
    }
}
