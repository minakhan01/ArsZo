package cafe.adriel.androidaudiorecorder.example;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO = 0;
    private static final String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/recorded_audio.wav";
    RequestQueue queue;
    Button sendButton;
    EditText textField;
    private MediaPlayer mMediaPlayer = null;
    String filePath =
            Environment.getExternalStorageDirectory().getPath() + "/Download/out.mp3";

    //View.OnClickListener sendButtonListener;

    private void recognizeSpeech() {
        //read filePath and it to server
        String url= "https://iceus.azure-api.net/intern/event/api/recognize";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("recognizeSpeech", response);
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
                                JSONObject obj = new JSONObject("{"+res+"}");
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
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

                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;

                File file = new File(filePath);
                try {
                    byte[] buf;
                    FileInputStream fileInput = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInput);
                    buf = BufferedByteStream.toByteArray(bufferedInputStream);
                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(buf);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    try {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return bos.toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                try {
//                    // populate text payload
//                    Map<String, String> params = getParams();
//                    if (params != null && params.size() > 0) {
//                        textParse(dos, params, getParamsEncoding());
//                    }
//
//                    // populate data byte payload
//                    Map<String, DataPart> data = getByteData();
//                    if (data != null && data.size() > 0) {
//                        dataParse(dos, data);
//                    }
//
//                    // close multipart form data after text and file data
//                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//                    return bos.toByteArray();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

                return params;
            }
        };
        queue.add(postRequest);
    }
    private void playFile() {
            mMediaPlayer = new MediaPlayer();

            try {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                        Log.d("MP3", "mMediaPlayer.start()");
                    }
                });
            } catch (IOException e) {
                Log.e("playFioe", "prepare() failed");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
        }

        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        textField = (EditText) findViewById(R.id.text_field);

        sendButton = (Button) findViewById(R.id.send_button) ;
        sendButton.setOnClickListener(sendButtonListener);

        queue = Volley.newRequestQueue(this);
        //simpleRequest();
        stringRequest();
        jsonRequest();
        voiceRequestJson();
        voiceRequestJsonToMp3();
        recognizeSpeech();
    }

    protected View.OnClickListener sendButtonListener = new View.OnClickListener(){
        public void onClick(View v) {
            Log.d("HELLO", "send button pressed");
            String textMessage = textField.getText().toString();
            sendMessage(textMessage);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void recordAudio(View v) {
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(AUDIO_FILE_PATH)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
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

    private void simpleRequest() {
        // Instantiate the RequestQueue.

        String url ="http://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("HELLO", "Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HELLO", "That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void headerRequest() {
        String url= "https://iceus.azure-api.net/intern/event/api/chat";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    /* import com.android.volley.toolbox.HttpHeaderParser; */
                    public void onErrorResponse(VolleyError error) {

                        // As of f605da3 the following should work
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Ocp-Apim-Trace", "true");
                params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void stringRequest() {
        String url= "https://" +
                "iceus.azure-api.net/intern/event/api/chat";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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
                                JSONObject obj = new JSONObject("{"+res+"}");
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("TextMessage", "rhyme with me Zo");
                params.put("UserId", "arsTechnicaDemoUser");
                params.put("ClientId", "facebook");
                params.put("IsVoice", "true");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Ocp-Apim-Trace", "true");
                params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void voiceRequestJson() {
        String url= "https://iceus.azure-api.net/intern/event/api/voice";
        //?text=do+you+have+anything+real+to+say%3F&outputFormat=audio-16khz-32kbitrate-mono-mp3";
        final String t = "hello please";
        try {
            String query = URLEncoder.encode("do you have anything real to say", "utf-8");
            url +=  "?text=" + query;
            url += "&outputFormat=audio-16khz-32kbitrate-mono-mp3";
            Log.d("URL","url: "+url);
//        byte[] u = t.getBytes("ISO-8859-1");
//        final String utf8String = new String(u, "UTF-8");
            StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // As of f605da3 the following should work
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    JSONObject obj = new JSONObject(responseBody);
                                    Log.d("JSON", obj.toString());
                                } catch (JSONException e) {
                                    //Handle a malformed json response
                                } catch (UnsupportedEncodingException e) {

                                }
                            }
                        }
                    }
            ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("outputFormat", "audio-16khz-32kbitrate-mono-mp3");
//                params.put("text", t);
//
//                return params;
//            }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    //params.put("Content-Type", "application/json");
                    params.put("Ocp-Apim-Trace", "true");
                    params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

                    return params;
                }
            };
            Log.d("POST", "postRequest: " + postRequest.toString());
            queue.add(postRequest);
        }
        catch (UnsupportedEncodingException  e) {}
    }

    private void voiceRequestJsonToMp3() {
        String url= "https://iceus.azure-api.net/intern/event/api/voice";
        //?text=do+you+have+anything+real+to+say%3F&outputFormat=audio-16khz-32kbitrate-mono-mp3";
        final String t = "hello please";
        final int BUFFER_SIZE = 1024;
        final int BUFFER_OFFSET = 0;
        final int READ_FAILED = -1;
        try {
            String query = URLEncoder.encode("do you have anything real to say", "utf-8");
            url +=  "?text=" + query;
            url += "&outputFormat=audio-16khz-32kbitrate-mono-mp3";
            Log.d("URL","url: "+url);
//        byte[] u = t.getBytes("ISO-8859-1");
//        final String utf8String = new String(u, "UTF-8");
            InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                    new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            if (response != null) {
                                FileOutputStream outStream = null;
                                InputStream inStream = null;
                                try {
                                    String directory = Environment.getExternalStorageDirectory().getPath() +
                                            "/Download/";
                                    Log.d("MP3", "directory: "+directory);
                                    File file = new File(directory);
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    File outputFile = new File(file,
                                            "out.mp3");

                                    if (outputFile.exists()) {
                                        outputFile.delete();
                                    }

                                    outStream = new FileOutputStream(outputFile);
                                    inStream = new ByteArrayInputStream(response);

                                    byte[] buffer = new byte[BUFFER_SIZE];
                                    int lengthFile;

                                    while ((lengthFile = inStream.read(buffer)) != READ_FAILED) {
                                        outStream.write(buffer, BUFFER_OFFSET, lengthFile);
                                    }

                                    String filePath = directory + "/Download/";
                                    Uri fileUri = Uri.fromFile(new File(filePath));
                                    //startInstallerIntent(fileUri);
                                } catch (IOException e) {
                                    Log.e("MP3", "File download/save failure in AppUpdator.", e);
                                } catch (IllegalArgumentException e) {
                                    Log.e("MP3", "Error occurred while sending 'Get' request due to empty host name");
                                } finally {
                                    StreamHandler.closeOutputStream(outStream, "MP3");
                                    StreamHandler.closeInputStream(inStream, "MP3");
                                    Log.d("MP3", "playFile");
                                    playFile();
                                    recognizeSpeech();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("MP3", error.toString());
                        }
                    }, null);
            queue.add(request);
        }
        catch (UnsupportedEncodingException  e) {}
    }

    private void voiceRequest() {
        String url= "https://iceus.azure-api.net/intern/event/api/voice";
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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
                                //JSONObject obj = new JSONObject("{"+res+"}");
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
                params.put("text", "rhyme with me Zo");
                params.put("outputFormat", "audio-16khz-32kbitrate-mono-mp3");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Ocp-Apim-Trace", "true");
                params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void jsonRequest() {
        String url= "https://" +
                "iceus.azure-api.net/intern/event/api/chat";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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
                                JSONObject obj = new JSONObject("{"+res+"}");
                                obj.getString("TextReply");
                                JSONArray delayedReplies = obj.getJSONArray("DelayedResponses");
                                for (int i = 0; i < delayedReplies.length(); i++) {
                                    JSONObject delayedReply = delayedReplies.getJSONObject(i);
                                    JSONArray textReplies = delayedReply.getJSONArray("TextReplies");
                                    for (int j = 0; j < textReplies.length(); j++) {
                                        JSONObject textReply = textReplies.getJSONObject(j);
                                        String reply = textReply.getString("TextReplies");
                                        Log.d("ZO", "reply: "+reply);
                                    }
                                }

                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("TextMessage", "rhyme with me Zo");
                params.put("UserId", "arsTechnicaDemoUser");
                params.put("ClientId", "facebook");
                params.put("IsVoice", "true");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Ocp-Apim-Trace", "true");
                params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void sendMessage(final String message) {
        String url= "https://" +
                "iceus.azure-api.net/intern/event/api/chat";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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
                                JSONObject obj = new JSONObject("{"+res+"}");
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("TextMessage", message);
                params.put("UserId", "arsTechnicaDemoUser");
                params.put("ClientId", "facebook");
                params.put("IsVoice", "true");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Ocp-Apim-Trace", "true");
                params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

                return params;
            }
        };
        queue.add(postRequest);
    }

}


class InputStreamVolleyRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;
    private Map<String, String> mParams;

    //create a static map for directly accessing headers
    public Map<String, String> responseHeaders ;

    public InputStreamVolleyRequest(int method, String mUrl ,Response.Listener<byte[]> listener,
                                    Response.ErrorListener errorListener, HashMap<String, String> params) {
        // TODO Auto-generated constructor stub

        super(method, mUrl, errorListener);
        // this request would never use cache.
        setShouldCache(false);
        mListener = listener;
        mParams=params;
    }

    @Override
    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return mParams;
    };


    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {

        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers;

        //Pass the response data here
        return Response.success( response.data, HttpHeaderParser.parseCacheHeaders(response));
    }


        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        //params.put("Content-Type", "application/json");
        params.put("Ocp-Apim-Trace", "true");
        params.put("Ocp-Apim-Subscription-Key", "94fee08703ef40909632ec84a1104df8");

        return params;
    }
}

/**
 * This class handles the closure of all the stream types.
 */
class StreamHandler {

    /**
     * Close a ByteArrayOutputStream passed in.
     *
     * @param stream
     *            - ByteArrayOutputStream to be closed.
     */
    public static void closeOutputStream(OutputStream stream, String tag) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(tag, "Exception occured when closing ByteArrayOutputStream." + e);
            }
        }
    }

    /**
     * Close a InputStream passed in.
     *
     * @param stream
     *            - InputStream to be closed.
     */
    public static void closeInputStream(InputStream stream, String tag) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(tag, "Exception occured when closing InputStream", e);
            }
        }
    }

    /**
     * Close a InputStream passed in.
     *
     * @param stream
     *            - InputStream to be closed.
     */
    public static void closeBufferedReader(BufferedReader stream, String tag) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(tag, "Exception occured when closing BufferedReader", e);
            }
        }
    }

}

class BufferedByteStream {

    private static final int BUF_SIZE = 1024 *1024;

    public static long copy(BufferedInputStream from, BufferedOutputStream to) throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        long total = 0;
        while(true) {
            int r = from.read(buf);
            if(r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }

    public static byte[] toByteArray(BufferedInputStream in) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BufferedOutputStream out = new BufferedOutputStream(bytesOut);
        copy(in, out);
        return bytesOut.toByteArray();
    }
}