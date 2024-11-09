package client;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import packet.tcp.TCPPacket;
import packet.udp.UDPVideoControlPacket;
import service.core.connection.ConnectionWorker;
import service.core.struct.BoundedBuffer;
import service.establishconnection.WaitEstablishConnection;
import service.gather.BootstrapperGather;


public class Client {
    public static final int MAX_PACKET_SIZE = 1000;
    public static final int CLIENT_CONNECTION_PORT = 19999;

    public static void main(String[] args) throws InterruptedException, IOException{

        String nodeName = args[0];
        String bootstrapperIP = args[1];
        
        BootstrapperGather bootstrapperGather = new BootstrapperGather(nodeName,bootstrapperIP);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();
        
        List<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toList());
        
        System.out.println(neighbours);
        String neighbour = neighbours.get(0);

        try (DatagramSocket socket = new DatagramSocket(CLIENT_CONNECTION_PORT)){

        UDPVideoControlPacket packet = new UDPVideoControlPacket(UDPVideoControlPacket.VIDEO_PROTOCOL.REQUEST,neighbour,"");
        DatagramPacket udp_packet = new DatagramPacket(packet.serialize(),packet.serialize().length, InetAddress.getByName(packet.getReceiver()),CLIENT_CONNECTION_PORT);
        socket.send(udp_packet);

        byte[] packetBuffer = new byte[MAX_PACKET_SIZE];
        DatagramPacket receivedSerializedPacket = new DatagramPacket(packetBuffer, MAX_PACKET_SIZE);
        socket.receive(receivedSerializedPacket);
        UDPVideoControlPacket receivedPacket = UDPVideoControlPacket.deserialize(receivedSerializedPacket.getData());
        System.out.println("Received packet " + receivedPacket);
        }

    }
}