package cafe.adriel.androidaudiorecorder.example;

import java.util.UUID;

/**
 * Created by minakhan on 8/26/17.
 */

public class BLEInstance {

    public static String deviceName;
    public static String deviceAddress;

    private BLEInstance() {
    }

    private static volatile BLEInstance sInstance;

    public static BLEInstance INSTANCE() {

        if  (sInstance == null) {
            synchronized (NetworkUtil.class) {
                if (sInstance == null) {
                    sInstance = new BLEInstance();
                }
            }
        }

        return sInstance;
    }

}
