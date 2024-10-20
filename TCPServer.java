import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer{

    private static final int BUFFER_SIZE = 4096;
    private static final int TPC_PORT = 12345;

    public static void main(String args[]) throws IOException{

        Socket socket;
        ServerSocket serverSocket = new ServerSocket(TPC_PORT);

        int bytes_read;
        byte[] buffer = new byte[BUFFER_SIZE];
        FileInputStream fileInputStream = new FileInputStream(args[0]);

        while ((socket = serverSocket.accept()) != null){

            int counter = 0;
            OutputStream outputStream = socket.getOutputStream();

            while ((bytes_read = fileInputStream.read(buffer)) > 0){
                outputStream.write(buffer,0,bytes_read);
                counter++;
            }

            System.out.println("Pacotes enviados: " + counter);
            fileInputStream.getChannel().position(0);
            outputStream.close();
            socket.close();
        }

        serverSocket.close();
        fileInputStream.close();
    }
}