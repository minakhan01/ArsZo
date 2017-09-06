package cafe.adriel.androidaudiorecorder.example;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import cafe.adriel.androidaudiorecorder.*;
import cafe.adriel.androidaudiorecorder.Util;

/**
 * Created by minakhan on 8/24/17.
 */

public class PrivacyActivity extends AppCompatActivity {

    Button startChatButton;
    TextView privacyText;

    MediaPlayer mediaPlayer;
    int[] soundResources = new int[] {R.raw.one, R.raw.two, R.raw.three, R.raw.four, R.raw.five};
    int random;

    private RelativeLayout contentLayout;
    private LinearLayout mLinearLayout;
    private GLAudioVisualizationView visualizerView;
    private TextView mTextView;

    Intent gattServiceIntent1;
    private RBLService mBluetoothLeService;
    ImageView mImageView;
    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        bar.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.my_custom_title, null);
        bar.setCustomView(v);

        setContentView(R.layout.activity_privacy);

        visualizerView = new GLAudioVisualizationView.Builder(this)
                .setLayersCount(1)
                .setWavesCount(6)
                .setWavesHeight(cafe.adriel.androidaudiorecorder.R.dimen.aar_wave_height)
                .setWavesFooterHeight(cafe.adriel.androidaudiorecorder.R.dimen.aar_footer_height)
                .setBubblesPerLayer(20)
                .setBubblesSize(cafe.adriel.androidaudiorecorder.R.dimen.aar_bubble_size)
                .setBubblesRandomizeSize(true)
                .setBackgroundColor(Color.WHITE)
                .setLayerColors(new int[]{ContextCompat.getColor(this, R.color.green)
                        , Color.BLACK})
                .build();

        contentLayout = (RelativeLayout) findViewById(cafe.adriel.androidaudiorecorder.R.id.content);
        mLinearLayout = (LinearLayout) findViewById(R.id.terms_button);
//        mLinearLayout.setVisibility(View.INVISIBLE);
        contentLayout.setBackgroundColor(Color.WHITE);
        contentLayout.addView(visualizerView, 0);
        mTextView = (TextView) findViewById(R.id.privacy_terms);
        mImageView = (ImageView) findViewById(R.id.image);
        mTextView.setVisibility(View.INVISIBLE);

        Intent mIntent = getIntent();
        random = mIntent.getIntExtra("randomValue", 0);
        Log.d("Privacy", random+" random");
        privacyText = (TextView) findViewById(R.id.privacy_terms);

        startChatButton = (Button) findViewById(R.id.start_chat_button);
        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PrivacyActivity.this, MainActivity.class);
                PrivacyActivity.this.startActivity(myIntent);
            }
        });
        gattServiceIntent1 = new Intent(getApplicationContext(), RBLService.class);
        managerOfSound();

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(3000);
        anim.setStartOffset(5000);
        anim.setFillAfter(true);
        mTextView.startAnimation(anim);
        mTextView.setText(R.string.terms_string);

        AlphaAnimation anim1 = new AlphaAnimation(0.0f, 1.0f);
        anim1.setDuration(3000);
        anim1.setStartOffset(5000);
        anim1.setFillAfter(true);
        mImageView.startAnimation(anim);
//        mImageView.setImageDrawable(R.string.terms_string);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("welcome activity", "on resume");
        getApplicationContext().bindService(gattServiceIntent1, mServiceConnection, BIND_AUTO_CREATE);
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        getApplicationContext().unbindService(mServiceConnection);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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



    private void managerOfSound() {

//        Random r = new Random();
//        random = r.nextInt(soundResources.length - 1);

        mediaPlayer = MediaPlayer.create(this, soundResources[random]);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            sendBLEMessage("start");
            Log.d("MEDIA", "start");
        } else {
            mediaPlayer.stop();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                visualizerView.setVisibility(View.INVISIBLE);
                mLinearLayout.setVisibility(View.VISIBLE);
                Log.d("MEDIA", "stop");
                mp.reset();
                mp.release();
                sendBLEMessage("end");
            }
        });
        visualizerView.linkTo(DbmHandler.Factory.newVisualizerHandler(this, mediaPlayer));
        visualizerView.post(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        Log.d("MEDIA", "stop");
                        mp.reset();
                        mp.release();
                        sendBLEMessage("end");
                    }
                });
            }
        });
    }
}
