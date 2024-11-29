package client;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import carrier.UDPCarrier;
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

            UDPVideoListPacket reply = null;
            UDPVideoListPacket request = new UDPVideoListPacket();

            udpCarrier.connect(socketAddress);
            udpCarrier.send(request);
            udpCarrier.disconnect();

            while (reply == null){
                reply = (UDPVideoListPacket) udpCarrier.receive();
            }

            udpCarrier.close();
            return reply.getVideos();
        }

        catch (SocketException e){
            System.out.println("Can not contact: " + this.edgeNodeIP);
            return null;
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}