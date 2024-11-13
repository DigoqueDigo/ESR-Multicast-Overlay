package service.establishconnection;
import packet.tcp.TCPPacket;
import packet.udp.UDPPacket;
import service.core.stream.StreamWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.MapBoundedBuffer;
import java.net.InetSocketAddress;
import carrier.UDPCarrier;


public class ClientWaitEstablishConnection implements Runnable{

    public static final int CLIENT_ESTABLISH_CONNECTION_PORT = 5000;

//    private BoundedBuffer<TCPPacket> inBuffer;
    private MapBoundedBuffer<String,byte[]> videoBuffers;


    public ClientWaitEstablishConnection(BoundedBuffer<TCPPacket> inBuffer, MapBoundedBuffer<String,byte[]> videoBuffers){
 //       this.inBuffer  = inBuffer;
        this.videoBuffers = videoBuffers;
    }


    public void run(){

        try{

            UDPPacket udpPacket;
            InetSocketAddress socketAddress = new InetSocketAddress(CLIENT_ESTABLISH_CONNECTION_PORT);
            UDPCarrier udpCarrier = new UDPCarrier(socketAddress);

            System.out.println("ClientWaitEstablishConnection service started");

            while (udpCarrier.isClosed() == false){

                if ((udpPacket = udpCarrier.receive()) != null){

                    System.out.println("ClientWaitEstablishConnection received packet: " + udpPacket);

                    String client = udpPacket.getSender();
                    this.videoBuffers.addBoundedBuffer(client);

                    BoundedBuffer<byte[]> videoBuffer = this.videoBuffers.getBoundedBuffer(client);
                    StreamWorker streamWorker = new StreamWorker(client,videoBuffer);

                    new Thread(streamWorker).start();

                    // converter o udpPacket para um formato TCP e espetar o gajo no inbuffer
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }    
    }
}