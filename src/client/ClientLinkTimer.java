package client;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.TimerTask;
import carrier.UDPCarrier;
import node.stream.NodeStreamWaitClient;
import packet.udp.UDPLinkPacket;
import struct.EdgeProviders;


public class ClientLinkTimer extends TimerTask{

    public static final Long DELAY = 1_000L;
    public static final Long PERIOD = 15_000L;

    private List<String> edgeNodes;
    private EdgeProviders edgeProviders;


    public ClientLinkTimer(List<String> edgeNodes, EdgeProviders edgeProviders){
        this.edgeNodes = edgeNodes;
        this.edgeProviders = edgeProviders;
    }


    public void run(){

        try{

            UDPCarrier udpCarrier = new UDPCarrier();

            for (String edgeNode : this.edgeNodes){

                try{

                    UDPLinkPacket reply = null;
                    UDPLinkPacket request = new UDPLinkPacket();
                    InetSocketAddress socketAddress = new InetSocketAddress(edgeNode,NodeStreamWaitClient.CLIENT_ESTABLISH_CONNECTION_PORT);

                    udpCarrier.connect(socketAddress);
                    int attempts = udpCarrier.send(request);
                    udpCarrier.disconnect();

                    while (reply == null){
                        reply = (UDPLinkPacket) udpCarrier.receive();
                    }

                    Float loss = Float.valueOf(1 - 1f / attempts);
                    Float rtt = Float.valueOf(reply.getTimestamp() - request.getTimestamp());

                    this.edgeProviders.addLoss(edgeNode,loss);
                    this.edgeProviders.addRTT(edgeNode,rtt);
                }

                catch (SocketException e){
                    udpCarrier.disconnect();
                    this.edgeProviders.addLoss(edgeNode,1f);
                    this.edgeProviders.addRTT(edgeNode,Float.POSITIVE_INFINITY);
                }
            }

            udpCarrier.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}