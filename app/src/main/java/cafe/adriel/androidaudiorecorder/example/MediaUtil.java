package cafe.adriel.androidaudiorecorder.example;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by minakhan on 8/17/17.
 */

public class MediaUtil {
    private MediaUtil() {
    }

    private static volatile MediaUtil sInstance;

    public static MediaUtil INSTANCE() {

        if  (sInstance == null) {
            synchronized (FileUtil.class) {
                if (sInstance == null) {
                    sInstance = new MediaUtil();
                }
            }
        }

        return sInstance;
    }

    public void recordUserFile() {
        NetworkUtil.INSTANCE().recognizeSpeech();
    }

    public void playZoFile() {
        MainActivity.currentAppState = AppState.PLAYING_ZO_REPLY;
        final MediaPlayer mMediaPlayer = MainActivity.sMediaPlayer;

        try {
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
        MediaPlayer mMediaPlayer = MainActivity.sMediaPlayer;
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

    }
}
