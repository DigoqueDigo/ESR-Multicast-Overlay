import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient{

    private static final int BUFFER_SIZE = 4096;
    private static final int TPC_PORT = 12345;
    private static final String TCP_ADDRESS = "localhost"; 

    public static void main(String args[]) throws IOException, InterruptedException{

        Socket socket = new Socket(TCP_ADDRESS,TPC_PORT);

        //ProcessBuilder processBuilder = new ProcessBuilder("mpv","-");
        //Process process = processBuilder.start();

        FileOutputStream fifo = new FileOutputStream("fifo");
        //InputStream error = process.getErrorStream();
        //OutputStream outputStream = process.getOutputStream();


        
        InputStream inputStream = socket.getInputStream();
        int bytes_read;
        byte[] buffer = new byte[BUFFER_SIZE];

        while ((bytes_read = inputStream.read(buffer)) > 0){
            fifo.write(buffer,0,bytes_read);
        //    bytes_read = error.read(buffer);
          //  fifo.write(buffer,0,bytes_read);
            
        }

        fifo.close();
        inputStream.close();
      //  outputStream.close();
        
        //process.waitFor();
        socket.close();
    }
}