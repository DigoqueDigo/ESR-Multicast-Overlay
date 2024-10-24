package bootstrapper;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;


public class Bootstrapper{

    private static final int PORT = 3000;


    public static void main(String args[]) throws IOException, InterruptedException{

        String json_content = new String(Files.readAllBytes(Paths.get("config/" + args[0])));
        JSONObject jsonObject = new JSONObject(json_content);
        int expected_clients = jsonObject.length();

        Socket socket;
        ServerSocket serverSocket = new ServerSocket(PORT);
        List<Thread> workers = new ArrayList<>();

        while (expected_clients > 0 && (socket = serverSocket.accept()) != null){
            Thread worker = new Thread(new BootstrapperWorker(socket,jsonObject));
            workers.add(worker);
            worker.start();
            expected_clients--;
        }

        for (Thread worker : workers){
            worker.join();
        }

        serverSocket.close();
    }
}