package cafe.adriel.androidaudiorecorder.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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