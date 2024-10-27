package server;
import java.io.IOException;
import java.net.Socket;
import bootstrapper.Bootstrapper;
import carrier.TCPCarrier;
import packet.tcp.TCPBootstrapperPacket;


public class Server {

    public static void main(String[] args) throws InterruptedException, IOException{

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        Socket socket = new Socket(bootstrapperIP,Bootstrapper.PORT);
        TCPCarrier tcpCarrier = new TCPCarrier(socket.getInputStream(),socket.getOutputStream());

        TCPBootstrapperPacket request = new TCPBootstrapperPacket(nodeName);
        tcpCarrier.send(request);

        TCPBootstrapperPacket response = (TCPBootstrapperPacket) tcpCarrier.receive();
        System.out.println(response.getJsonObject().getJSONArray("neighbours"));

        socket.close();
    }
}