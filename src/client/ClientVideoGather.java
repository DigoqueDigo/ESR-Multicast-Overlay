package client;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.util.List;
import carrier.UDPCarrier;
import packet.udp.UDPPacket;
import packet.udp.UDPVideoListPacket;


public class ClientVideoGather{

    private String edgeNodeIP;
    private int edgeNodePort;


    public ClientVideoGather(String edgeNodeIP, int edgeNodePort){
        this.edgeNodeIP = edgeNodeIP;
        this.edgeNodePort = edgeNodePort;
    }


    public List<String> getVideoList(){

        try{

            UDPCarrier udpCarrier = new UDPCarrier();
            InetSocketAddress socketAddress = new InetSocketAddress(this.edgeNodeIP,this.edgeNodePort);

            UDPPacket udpPacket = null;
            UDPVideoListPacket videoListPacket = new UDPVideoListPacket();

            System.out.println("ClientVideoGather send: " + videoListPacket);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoListPacket);
            udpCarrier.disconnect();

            while ((udpPacket = udpCarrier.receive()) == null) {}

            videoListPacket = (UDPVideoListPacket) udpPacket;
            System.out.println("ClientVideoGather receive: " + videoListPacket);

            udpCarrier.close();
            return videoListPacket.getVideos();
        }

        catch (PortUnreachableException e){
            System.out.println("Can not contact: " + this.edgeNodeIP + ":" + this.edgeNodePort);
            return null;
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}