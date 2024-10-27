package node;

import bootstrapper.Bootstrapper;
import carrier.TCPCarrier;
import org.json.JSONArray;
import packet.tcp.TCPBootstrapperPacket;

import java.io.IOException;
import java.net.Socket;

public class Node {

    public static void main(String[] args) throws InterruptedException, IOException {

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        Socket socket = new Socket(bootstrapperIP, Bootstrapper.PORT);
        TCPCarrier tcpCarrier = new TCPCarrier(socket.getInputStream(),socket.getOutputStream());

        TCPBootstrapperPacket request = new TCPBootstrapperPacket(nodeName);
        tcpCarrier.send(request);

        TCPBootstrapperPacket response = (TCPBootstrapperPacket) tcpCarrier.receive();
        JSONArray neighbours = response.getJsonObject().getJSONArray("neighbours");
        System.out.println(neighbours);

        socket.close();
    }
}
