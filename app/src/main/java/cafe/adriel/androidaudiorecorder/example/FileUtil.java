package cafe.adriel.androidaudiorecorder.example;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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

/**
 * Created by minakhan on 8/17/17.
 */

public class FileUtil {

    private FileUtil() {
    }

    private static volatile FileUtil sInstance;

    public static FileUtil INSTANCE() {

        if  (sInstance == null) {
            synchronized (FileUtil.class) {
                if (sInstance == null) {
                    sInstance = new FileUtil();
                }
            }
        }

        return sInstance;
    }

    final int BUFFER_SIZE = 1024;
    final int BUFFER_OFFSET = 0;
    final int READ_FAILED = -1;

    public void writeFile(byte[] response) {
        FileOutputStream outStream = null;
        InputStream inStream = null;
        try {
            String directory = MainActivity.DOWNLOAD_PATH;
            Log.d("MP3", "directory: "+directory);
            File file = new File(directory);
            if (!file.exists()) {
                file.mkdirs();
            }
            File outputFile = new File(file, MainActivity.ZO_OUT_FILE);

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

        } catch (IOException e) {
            Log.e("MP3", "File download/save failure in AppUpdator.", e);
        } catch (IllegalArgumentException e) {
            Log.e("MP3", "Error occurred while sending 'Get' request due to empty host name");
        } finally {
            StreamHandler.closeOutputStream(outStream, "MP3");
            StreamHandler.closeInputStream(inStream, "MP3");
            Log.d("MP3", "playFile");
            MediaUtil.INSTANCE().playZo();
        }
    }

    public byte[] readUserInputFile() {
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File file = new File(MainActivity.DOWNLOAD_PATH + MainActivity.USER_IN_FILE);
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
        return null;
    }

}
