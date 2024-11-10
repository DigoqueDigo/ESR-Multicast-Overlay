package carrier;
import java.io.IOException;
import java.io.InputStream;


public final class IOUtils{

    public static int readAllBytes(InputStream inputStream, byte[] data, int length) throws IOException{

        int  attempt = 1;
        int bytes_read = 0;

        for (int rest = length; attempt > 0 && bytes_read < length; rest -= attempt){

            attempt = inputStream.read(data,bytes_read,rest);
            if (attempt > 0) bytes_read += attempt;
        }

        return bytes_read;
    }
}