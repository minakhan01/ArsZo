package cafe.adriel.androidaudiorecorder.example;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by minakhan on 8/17/17.
 */

public class NetworkUtil {

    private static final String VOICE_OUTPUT_URL = "https://iceus.azure-api.net/intern/event/api/voice";
    private static final String TEXT_URL = "https://iceus.azure-api.net/intern/event/api/ars/electronica/chat";
    private static final String SPEECH_RECOGNITION_URL = "https://iceus.azure-api.net/intern/event/api/recognize";

    private static final String CLIENT_ID_TAG = "ClientId";
    private static final String USER_ID_TAG = "UserId";
    private static final String IS_VOICE_TAG = "IsVoice";
    private static final String OCP_TRACE_TAG = "Ocp-Apim-Trace";
    private static final String OCP_KEY_TAG = "Ocp-Apim-Subscription-Key";
    private static final String TEXT_MSG_TAG = "TextMessage";

    String clientID = "facebook";
    static String userID = "arsTechnicaDemoUser";
    String trueValue = "true";
    String subscriptionKey = "94fee08703ef40909632ec84a1104df8";

    String voiceOutputTag = "&outputFormat=audio-16khz-32kbitrate-mono-mp3";


    private NetworkUtil() {
    }

    private static volatile NetworkUtil sInstance;

    public static NetworkUtil INSTANCE() {

        if  (sInstance == null) {
            synchronized (NetworkUtil.class) {
                if (sInstance == null) {
                    sInstance = new NetworkUtil();
                    userID = UUID.randomUUID().toString();
                }
            }
        }

        return sInstance;
    }

    public void SendFirstTextQueryToServer(final String msg) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, TEXT_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if (response != null) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                String reply = obj.getString("TextReply");
                                //MainActivity.zoTextView.setText(reply);
                                Log.d("REPLY", reply);
                                SendTextQueryToServer("Rhyme with me Zo");
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // As of f605da3 the following should work
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {

                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put(TEXT_MSG_TAG, msg);
                params.put(USER_ID_TAG, userID);
                params.put(CLIENT_ID_TAG, clientID);
                params.put(IS_VOICE_TAG, trueValue);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put(OCP_TRACE_TAG, trueValue);
                params.put(OCP_KEY_TAG, subscriptionKey);

                return params;
            }
        };
        MainActivity.queue.add(postRequest);
    }

    public void SendTextQueryToServer(final String msg) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, TEXT_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if (response != null) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                String reply = obj.getString("TextReply");
                                String skillLevel = obj.getString("TextReplySource");
                                int responseStateInt = StringUtil.INSTANCE().findReplyResponseInt(reply);
                                if (skillLevel.toLowerCase().contains(("Skill").toLowerCase())) {
                                    MainActivity.mode = 1;
                                }
                                else {
                                    MainActivity.mode = 2;
                                }
                                MainActivity.modeReplyNumber = responseStateInt;
                                Log.d("REPLY", "responseStateInt: "+responseStateInt);
                                MainActivity.zoTextView.setText(reply);
                                Log.d("REPLY", reply);
                                SendTextToSpeechQuery(reply);
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // As of f605da3 the following should work
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {

                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put(TEXT_MSG_TAG, msg);
                params.put(USER_ID_TAG, userID);
                params.put(CLIENT_ID_TAG, clientID);
                params.put(IS_VOICE_TAG, trueValue);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put(OCP_TRACE_TAG, trueValue);
                params.put(OCP_KEY_TAG, subscriptionKey);

                return params;
            }
        };
        MainActivity.queue.add(postRequest);
    }

    private void SendTextToSpeechQuery(String response) {
        voiceRequestJsonToMp3(response);
    }

    private void voiceRequestJsonToMp3(String response) {
        String url = VOICE_OUTPUT_URL;

        try {
            String query = URLEncoder.encode(response, "utf-8");
            url +=  "?text=" + query;
            url += voiceOutputTag;
            Log.d("URL","url: "+url);

            InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                    new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            if (response != null) {
                                FileUtil.INSTANCE().writeFile(response);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("MP3", error.toString());
                        }
                    }, null);
            MainActivity.queue.add(request);
        }
        catch (UnsupportedEncodingException  e) {}
    }

    public void recognizeSpeech() {
        //read filePath and it to server
        StringRequest postRequest = new StringRequest(Request.Method.POST, SPEECH_RECOGNITION_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("recognizeSpeech", response);
                        MainActivity.userTextView.setText(response);
                        SendTextQueryToServer(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // As of f605da3 the following should work
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                try {
                    params.put("data", getBody().toString());
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
                return params;
            }



            @Override
            public byte[] getBody() throws AuthFailureError {
                return FileUtil.INSTANCE().readUserInputFile();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

                return params;
            }
        };
        MainActivity.queue.add(postRequest);
    }
}
