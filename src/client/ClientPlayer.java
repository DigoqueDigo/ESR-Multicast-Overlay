package client;
import java.net.InetSocketAddress;
import carrier.UDPCarrier;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPVideoControlPacket.VIDEO_PROTOCOL;
import service.establishconnection.ClientWaitEstablishConnection;


public class ClientPlayer{

    private String edgeNode;
    private String video;


    public ClientPlayer(String edgeNode, String video){
        this.edgeNode = edgeNode;
        this.video = video; 
    }


    public void play(){

        try{

            UDPCarrier udpCarrier = new UDPCarrier();
            UDPVideoControlPacket videoControlPacket = new UDPVideoControlPacket(VIDEO_PROTOCOL.REQUEST,this.video);
            InetSocketAddress socketAddress = new InetSocketAddress(this.edgeNode,ClientWaitEstablishConnection.CLIENT_CONNECTION_PORT);

            System.out.println("Sending packet: " + videoControlPacket);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoControlPacket);
            udpCarrier.disconnect();
            udpCarrier.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}