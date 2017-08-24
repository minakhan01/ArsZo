package cafe.adriel.androidaudiorecorder.example;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.io.IOException;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO = 0;
    private static final String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/recorded_audio.wav";

    static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() +
            "/Download/";
    static final String ZO_OUT_FILE = "out.mp3";
    static final String USER_IN_FILE = "in.mp3";
    private MediaPlayer mMediaPlayer;

    static AppState currentAppState = AppState.PLAYING_ZO_REPLY;
    String initialText = "Rhyme with me Zo";
    static RequestQueue queue;

    static TextView userTextView;
    static TextView zoTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setBackgroundDrawable(
//                    new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
//        }

        userTextView = (TextView) findViewById(R.id.user_text_field);
        zoTextView = (TextView) findViewById(R.id.zo_text_field);

        mMediaPlayer = new MediaPlayer();

        MediaUtil.INSTANCE().setActivity(this);

        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        queue = Volley.newRequestQueue(this);

        NetworkUtil.INSTANCE().SendFirstTextQueryToServer(initialText);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
                Log.d("onActivityResult", "hey, recording finished");
                NetworkUtil.INSTANCE().recognizeSpeech();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void recordAudio(View v) {
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(DOWNLOAD_PATH + USER_IN_FILE)
                .setColor(ContextCompat.getColor(this, R.color.green))
                .setRequestCode(REQUEST_RECORD_AUDIO)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(false)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }

    public void playZoFile() {
        MainActivity.currentAppState = AppState.PLAYING_ZO_REPLY;

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(MainActivity.DOWNLOAD_PATH +
                    MainActivity.ZO_OUT_FILE);
            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    Log.d("MP3", "mMediaPlayer.start()");
                }
            });
        } catch (IOException e) {
            Log.e("playFile", "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });
    }

    private void stopPlaying() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

    }

}