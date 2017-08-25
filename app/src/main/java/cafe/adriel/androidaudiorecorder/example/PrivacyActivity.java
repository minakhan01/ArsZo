package cafe.adriel.androidaudiorecorder.example;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

/**
 * Created by minakhan on 8/24/17.
 */

public class PrivacyActivity extends AppCompatActivity {

    Button startChatButton;
    TextView privacyText;

    MediaPlayer mediaPlayer;
    int[] soundResources = new int[] {R.raw.one, R.raw.two, R.raw.three, R.raw.four,
            R.raw.five, R.raw.six, R.raw.seven, R.raw.eight, R.raw.nine, R.raw.ten, R.raw.eleven};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        privacyText = (TextView) findViewById(R.id.privacy_terms);

        managerOfSound();

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

    private void managerOfSound() {

        Random r = new Random();
        int random = r.nextInt(soundResources.length - 1);

        mediaPlayer = MediaPlayer.create(this, soundResources[random]);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
            }
        });
    }
}
