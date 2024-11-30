package bootstrapper;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;


public class Bootstrapper{

    public static final int PORT = 3000;

    public static void main(String args[]) throws IOException, InterruptedException{

        String json_content = new String(Files.readAllBytes(Paths.get(args[0])));
        JSONObject jsonObject = new JSONObject(json_content);

        Socket socket;
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Bootstrapper waiting for connection");

        while ((socket = serverSocket.accept()) != null){
            new Thread(new BootstrapperWorker(socket,jsonObject)).start();
        }

        serverSocket.close();
    }
}