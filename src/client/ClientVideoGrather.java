package client;
import java.net.InetSocketAddress;
import java.util.Set;
import carrier.UDPCarrier;
import packet.udp.UDPPacket;
import packet.udp.UDPVideoListPacket;


public class ClientVideoGrather{

    private String edgeNodeIP;
    private int edgeNodePort;


    public ClientVideoGrather(String edgeNodeIP, int edgeNodePort){
        this.edgeNodeIP = edgeNodeIP;
        this.edgeNodePort = edgeNodePort;
    }


    public Set<String> getVideoList(){

        try{

            UDPCarrier udpCarrier = new UDPCarrier();
            InetSocketAddress socketAddress = new InetSocketAddress(this.edgeNodeIP,this.edgeNodePort);

            UDPPacket udpPacket = null;
            UDPVideoListPacket videoListPacket = new UDPVideoListPacket();

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoListPacket);
            udpCarrier.disconnect();

            while ((udpPacket = udpCarrier.receive()) == null) {}

            videoListPacket = (UDPVideoListPacket) udpPacket;

            udpCarrier.close();
            return videoListPacket.getVideos();
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}