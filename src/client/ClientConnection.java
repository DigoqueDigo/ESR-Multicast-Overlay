package client;
import java.net.InetSocketAddress;
import carrier.UDPCarrier;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPVideoControlPacket.VIDEO_PROTOCOL;
import service.establishconnection.ClientWaitEstablishConnection;


public class ClientConnection{

    private String edgeNode;
    private String video;


    public ClientConnection(String edgeNode, String video){
        this.edgeNode = edgeNode;
        this.video = video; 
    }


    public void start(){

        try{
            System.out.println(edgeNode);
 
            UDPCarrier udpCarrier = new UDPCarrier();
            InetSocketAddress socketAddress = new InetSocketAddress(
                this.edgeNode,ClientWaitEstablishConnection.CLIENT_ESTABLISH_CONNECTION_PORT);

            UDPVideoControlPacket videoRequest = new UDPVideoControlPacket(VIDEO_PROTOCOL.REQUEST,this.video);
            UDPVideoControlPacket videoCancel = new UDPVideoControlPacket(VIDEO_PROTOCOL.CANCEL,this.video);

            System.out.println("ClientConnection send packet: " + videoRequest);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoRequest);
            udpCarrier.disconnect();

            Thread clientPlayer = new Thread(new ClientPlayer(this.video));
            clientPlayer.start();
            clientPlayer.join();

            System.out.println("ClientConnection send packet: " + videoCancel);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoCancel);

            udpCarrier.close();
            udpCarrier.disconnect();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}