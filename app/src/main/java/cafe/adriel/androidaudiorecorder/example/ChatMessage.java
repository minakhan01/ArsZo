package cafe.adriel.androidaudiorecorder.example;

import android.util.Log;

/**
 * Created by minakhan on 9/1/17.
 */

public class ChatMessage {

    private String content;
    private static boolean isMine;

    public ChatMessage(String content, boolean isMine) {
        this.content = content;
        this.isMine = isMine;
    }

    public String getContent() {
        return content;
    }

    public boolean isMine() {
//        Log.d("ChatMessage", " is mine: "+isMine);
        return isMine;
    }
}