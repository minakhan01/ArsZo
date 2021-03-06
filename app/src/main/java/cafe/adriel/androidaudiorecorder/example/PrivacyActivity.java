package cafe.adriel.androidaudiorecorder.example;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by minakhan on 8/24/17.
 */

public class PrivacyActivity extends AppCompatActivity {

    Button startChatButton;
    TextView privacyText;

    MediaPlayer mediaPlayer;
    int[] soundResources = new int[] {R.raw.one, R.raw.two, R.raw.three, R.raw.four, R.raw.five};
    int random;

    private String mDeviceName;
    private String mDeviceAddress;

    private RBLService mBluetoothLeService;
    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        privacyText = (TextView) findViewById(R.id.privacy_terms);

        managerOfSound();

        mDeviceAddress = BLEInstance.deviceAddress;
        mDeviceName = BLEInstance.deviceName;

        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        startChatButton = (Button) findViewById(R.id.start_chat_button);
        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PrivacyActivity.this, MainActivity.class);
                PrivacyActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void sendBLEMessage(String msg) {
        BluetoothGattCharacteristic characteristic = map
                .get(RBLService.UUID_BLE_SHIELD_TX);
        Log.d("RBL", RBLService.UUID_BLE_SHIELD_TX.toString());
        byte b = 0x00;

//        byte[] tmp = msg.getBytes();
//        byte[] tx = new byte[tmp.length + 1];
//        for (int i = 1; i < tmp.length + 1; i++) {
//            tx[i] = tmp[i - 1];
//        }
        byte[] tx = new byte[3];
        tx[0] = b;
        tx[1] = Byte.parseByte(Integer.toHexString(random), 16);
        // Xin can send different bytes based on the sendblemessage string
        if (msg.contains("start")) {
            tx[2] = 0x00;
        }
        else if (msg.contains("end")) {
            tx[2] = 0x01;
        }


        characteristic.setValue(tx);
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((RBLService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("BLE", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    private void managerOfSound() {

        Random r = new Random();
        random = r.nextInt(soundResources.length - 1);

        mediaPlayer = MediaPlayer.create(this, soundResources[random]);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            //sendBLEMessage("start");
        } else {
            mediaPlayer.stop();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                //sendBLEMessage("start");
            }
        });
    }
}
