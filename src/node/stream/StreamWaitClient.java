package node.stream;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
import packet.tcp.TCPVideoControlPacket.OVERLAY_VIDEO_PROTOCOL;
import packet.udp.UDPPacket;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPVideoControlPacket.EDGE_VIDEO_PROTOCOL;
import packet.udp.UDPVideoListPacket;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;
import struct.VideoProviders;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Set;
import carrier.UDPCarrier;


public class StreamWaitClient implements Runnable{

    public static final int CLIENT_ESTABLISH_CONNECTION_PORT = 5000;

    private VideoProviders videoProviders;
    private BoundedBuffer<TCPPacket> inBuffer;
    private MapBoundedBuffer<String,byte[]> streamBuffers;


    public StreamWaitClient(VideoProviders videoProviders, BoundedBuffer<TCPPacket> inBuffer, MapBoundedBuffer<String,byte[]> streamBuffers){
        this.videoProviders = videoProviders;
        this.inBuffer = inBuffer;
        this.streamBuffers = streamBuffers;
    }


    private void handleVideoControl(UDPVideoControlPacket videoControlPacket) throws InterruptedException{

        if (videoControlPacket.getProtocol() == EDGE_VIDEO_PROTOCOL.VIDEO_REQUEST){
            this.handleVideoControlRequest(videoControlPacket);
        }

        UDPVideoControlPacket udpVideoControlPacket = (UDPVideoControlPacket) videoControlPacket;
        TCPVideoControlPacket tcpVideoControlPacket = new TCPVideoControlPacket(
            OVERLAY_VIDEO_PROTOCOL.valueOf(udpVideoControlPacket.getProtocol().name()),
            udpVideoControlPacket.getVideo());

        tcpVideoControlPacket.setReceiverIP(udpVideoControlPacket.getReceiverIP());
        tcpVideoControlPacket.setSenderIP(udpVideoControlPacket.getSenderIP());

        this.inBuffer.push(tcpVideoControlPacket);
    }


    private void handleVideoControlRequest(UDPVideoControlPacket videoControlPacket){

        String client = videoControlPacket.getSenderIP();
        this.streamBuffers.addBoundedBuffer(client);

        BoundedBuffer<byte[]> streamBuffer = this.streamBuffers.getBoundedBuffer(client);
        StreamWorker streamWorker = new StreamWorker(client,streamBuffer);

        new Thread(streamWorker).start();
    }


    private void handleVideoList(UDPVideoListPacket videoListPacket) throws SocketException, IOException, ClassNotFoundException{

        String clientIP = videoListPacket.getSenderIP();
        int clientPort = videoListPacket.getSenderPort();

        Set<String> videos = this.videoProviders.getVideos();
        UDPVideoListPacket response = new UDPVideoListPacket(videos);

        UDPCarrier udpCarrier = new UDPCarrier();
        InetSocketAddress socketAddress = new InetSocketAddress(clientIP,clientPort);

        udpCarrier.connect(socketAddress);
        udpCarrier.send(response);
        udpCarrier.disconnect();
        udpCarrier.close();
    }


    public void run(){

        try{

            UDPPacket udpPacket;
            InetSocketAddress socketAddress = new InetSocketAddress(CLIENT_ESTABLISH_CONNECTION_PORT);
            UDPCarrier udpCarrier = new UDPCarrier(socketAddress);

            System.out.println("StreamWaitClient service started");

            while (udpCarrier.isClosed() == false){

                if ((udpPacket = udpCarrier.receive()) != null){

                    System.out.println("StreamWaitClient received packet: " + udpPacket);

                    switch (udpPacket.getType()) {

                        case VIDEO_CONTROL:
                            this.handleVideoControl((UDPVideoControlPacket) udpPacket);
                            break;

                        case VIDEO_LIST:                    
                            this.handleVideoList((UDPVideoListPacket) udpPacket);
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }    
    }
}