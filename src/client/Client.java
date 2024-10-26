package client;
import java.io.IOException;
import java.net.Socket;
import bootstrapper.Bootstrapper;
import carrier.TCPCarrier;
import packet.tcp.TCPNodeInfo;


public class Client {

    public static void main(String[] args) throws InterruptedException, IOException{

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        Socket socket = new Socket(bootstrapperIP,Bootstrapper.PORT);
        TCPCarrier tcpCarrier = new TCPCarrier(socket.getInputStream(),socket.getOutputStream());

        TCPNodeInfo request = new TCPNodeInfo(nodeName);
        tcpCarrier.send(request);

        TCPNodeInfo response = (TCPNodeInfo) tcpCarrier.receive();
        System.out.println(response.getJsonObject().getJSONArray("neighbours"));

        socket.close();
    }
}