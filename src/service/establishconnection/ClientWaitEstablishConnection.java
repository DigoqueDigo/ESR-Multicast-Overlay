package service.establishconnection;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
import packet.tcp.TCPVideoControlPacket.CORE_VIDEO_PROTOCOL;
import packet.udp.UDPPacket;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPPacket.UDP_TYPE;
import service.core.stream.StreamWorker;
import service.struct.BoundedBuffer;
import service.struct.MapBoundedBuffer;
import java.net.InetSocketAddress;
import carrier.UDPCarrier;


public class ClientWaitEstablishConnection implements Runnable{

    public static final int CLIENT_ESTABLISH_CONNECTION_PORT = 5000;

    private BoundedBuffer<TCPPacket> inBuffer;
    private MapBoundedBuffer<String,byte[]> videoBuffers;


    public ClientWaitEstablishConnection(BoundedBuffer<TCPPacket> inBuffer, MapBoundedBuffer<String,byte[]> videoBuffers){
        this.inBuffer  = inBuffer;
        this.videoBuffers = videoBuffers;
    }


    public void run(){

        try{

            UDPPacket udpPacket;
            InetSocketAddress socketAddress = new InetSocketAddress(CLIENT_ESTABLISH_CONNECTION_PORT);
            UDPCarrier udpCarrier = new UDPCarrier(socketAddress);

            System.out.println("ClientWaitEstablishConnection service started");

            while (udpCarrier.isClosed() == false){

                if ((udpPacket = udpCarrier.receive()) != null && udpPacket.getType() == UDP_TYPE.CONTROL_VIDEO){

                    System.out.println("ClientWaitEstablishConnection received packet: " + udpPacket);

                    String client = udpPacket.getSender();
                    this.videoBuffers.addBoundedBuffer(client);

                    BoundedBuffer<byte[]> videoBuffer = this.videoBuffers.getBoundedBuffer(client);
                    StreamWorker streamWorker = new StreamWorker(client,videoBuffer);

                    new Thread(streamWorker).start();

                    // converter o udpPacket para um formato TCP e espetar o gajo no inbuffer
                    UDPVideoControlPacket udpVideoControlPacket = (UDPVideoControlPacket) udpPacket;
                    TCPVideoControlPacket tcpVideoControlPacket = new TCPVideoControlPacket(
                        CORE_VIDEO_PROTOCOL.valueOf(udpVideoControlPacket.getProtocol().name()),
                        udpVideoControlPacket.getVideo(),
                        new byte[0],
                        udpVideoControlPacket.getReceiver(),
                        udpVideoControlPacket.getSender());

                    this.inBuffer.push(tcpVideoControlPacket);
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }    
    }
}