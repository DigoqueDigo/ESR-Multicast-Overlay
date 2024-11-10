package client;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import carrier.UDPCarrier;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPVideoControlPacket.VIDEO_PROTOCOL;
import service.establishconnection.ClientWaitEstablishConnection;
import service.gather.BootstrapperGather;
import java.net.InetSocketAddress;


public class Client{

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException{

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        BootstrapperGather bootstrapperGather = new BootstrapperGather(nodeName,bootstrapperIP);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();

        List<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toList());

        System.out.println(neighbours);
        String neighbour = neighbours.get(0);

        UDPCarrier udpCarrier = new UDPCarrier();
        UDPVideoControlPacket videoControlPacket = new UDPVideoControlPacket(VIDEO_PROTOCOL.REQUEST, "videoA.mp4");
        InetSocketAddress socketAddress = new InetSocketAddress(neighbour,ClientWaitEstablishConnection.CLIENT_CONNECTION_PORT);

        System.out.println("Sending packet: " + videoControlPacket);

        udpCarrier.connect(socketAddress);
        udpCarrier.send(videoControlPacket);
        udpCarrier.disconnect();
        udpCarrier.close();
    }
}