package cafe.adriel.androidaudiorecorder.example;

import android.app.Dialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by minakhan on 8/18/17.
 */

public class WelcomeActivity extends AppCompatActivity {
    Button startChatButton;

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 3000;

    int random;
    int[] soundResources = new int[] {R.raw.one, R.raw.two, R.raw.three, R.raw.four, R.raw.five};


    Intent gattServiceIntent1;
    private RBLService mBluetoothLeService;
    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((RBLService.LocalBinder) service)
                    .getService();
            BLEInstance.INSTANCE().mBluetoothLeService = mBluetoothLeService;
            if (!mBluetoothLeService.initialize()) {
                Log.e("BLE", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(BLEInstance.INSTANCE().deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private void sendBLEMessage(String msg) {
        BluetoothGattCharacteristic characteristic = BLEInstance.INSTANCE().bluetoothGattCharacteristic;
        Log.d("RBL", RBLService.UUID_BLE_SHIELD_TX.toString());
        byte b = 0x00;

        byte[] tx = new byte[3];
        tx[0] = b;
        tx[1] = Byte.parseByte(Integer.toHexString(random), 16);
        // Xin can send different bytes based on the sendblemessage string
        if (msg.contains("start")) {
            tx[2] = 0x00;
        } else if (msg.contains("end")) {
            tx[2] = 0x01;
        }

        characteristic.setValue(tx);
        BLEInstance.INSTANCE().mBluetoothLeService.writeCharacteristic(characteristic);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Random r = new Random();
        random = r.nextInt(soundResources.length - 1);

        bleStuff();
        startChatButton = (Button) findViewById(R.id.start_chat_button);
        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBLEMessage("start");
                Intent myIntent = new Intent(WelcomeActivity.this, PrivacyActivity.class);
                myIntent.putExtra("randomValue", random);
                WelcomeActivity.this.startActivity(myIntent);
            }
        });
        startChatButton.setEnabled(false);

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("RBL", "BroadcastReceiver");
            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                getGattService(mBluetoothLeService.getSupportedGattService());
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d("onreceive", intent.getByteArrayExtra(RBLService.EXTRA_DATA).toString());
            }
        }
    };
    private boolean gotGattService = false;

    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        BluetoothGattCharacteristic characteristic = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristicRx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        mBluetoothLeService.setCharacteristicNotification(characteristicRx,
                true);
        mBluetoothLeService.readCharacteristic(characteristicRx);
        gotGattService = true;
        if (gotGattService) {
            startChatButton.setEnabled(true);
            BLEInstance.INSTANCE().bluetoothGattCharacteristic = characteristic;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("welcome activity", "on resume");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        getApplicationContext().unbindService(mServiceConnection);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    private void bleStuff() {
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        scanLeDevice();
    }

    private void scanLeDevice() {
        new Thread() {

            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                try {
                    Thread.sleep(SCAN_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }.start();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null && device.getName() != null) {
                        Log.d("onLeScan", device.getName());
                        if (device.getName().contains("Blend")) {
                            Log.d("devicefound", device.getName());
                            BLEInstance.INSTANCE().deviceName = device.getName();
                            BLEInstance.INSTANCE().deviceAddress = device.getAddress();


                            gattServiceIntent1 = new Intent(getApplicationContext(), RBLService.class);
                            startService(gattServiceIntent1);
                            getApplicationContext().bindService(gattServiceIntent1, mServiceConnection, BIND_AUTO_CREATE);
                        }
                    }
                }
            });
        }
    };

}
