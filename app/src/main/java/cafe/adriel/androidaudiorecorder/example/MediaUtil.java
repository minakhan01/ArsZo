package cafe.adriel.androidaudiorecorder.example;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by minakhan on 8/17/17.
 */

public class MediaUtil {

    private MainActivity mainActivity;
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

    public void setActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void playZo() {
        mainActivity.playZoFile();
    }
}
