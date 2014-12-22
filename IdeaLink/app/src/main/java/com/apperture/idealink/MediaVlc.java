package com.apperture.idealink;
/**
 * Created by Pradeep on 08-12-2014.
 */


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MediaVlc extends Activity {

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    static TextView state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_vlc);
        state=(TextView)findViewById(R.id.state);
    }

    public static boolean displayState(String curState){
        try {
            state.setText(curState);
        }
        catch(Exception e){return false;}
        Log.d("MediaVLC", "statedisplay true");
        return true;
    }

    private void ensureDiscoverable() {
        if (Backend.mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
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
        displayState(Backend.append);
    }
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
    public void OnClick(View view)
    {Intent intent;
        switch(view.getId()){
            case R.id.key: intent = new Intent(this, Keyboard.class);
                startActivity(intent);break;
            case R.id.Home: intent = new Intent(this, Home.class);
                startActivity(intent);break;
        }
    }
    public void vlcClick(View view)
    {
        String cmd="Vlc!!";

        switch (view.getId())
        {
            case R.id.play: cmd=cmd+" !!";break;
            case R.id.prev: cmd=cmd+"p!!";break;
            case R.id.next: cmd=cmd+"n!!";break;
            case R.id.plus :cmd=cmd+"VolUp!!";break;
            case R.id.minus: cmd=cmd+"Voldwn!!";break;


        }
        cmd=cmd+"!!";
        Backend.mCommandService.write(cmd);
    }
}
