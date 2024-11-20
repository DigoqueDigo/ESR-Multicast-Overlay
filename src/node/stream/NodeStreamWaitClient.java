package node.stream;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
import packet.tcp.TCPVideoControlPacket.OVERLAY_VIDEO_PROTOCOL;
import packet.udp.UDPPacket;
import packet.udp.UDPPacket.UDP_TYPE;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPVideoControlPacket.EDGE_VIDEO_PROTOCOL;
import packet.udp.UDPVideoListPacket;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;
import struct.VideoProviders;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import carrier.UDPCarrier;


public class NodeStreamWaitClient implements Runnable{

    public static final int CLIENT_ESTABLISH_CONNECTION_PORT = 5000;

    private VideoProviders videoProviders;
    private BoundedBuffer<TCPPacket> inBuffer;
    private MapBoundedBuffer<String,byte[]> streamBuffers;


    public NodeStreamWaitClient(VideoProviders videoProviders, BoundedBuffer<TCPPacket> inBuffer, MapBoundedBuffer<String,byte[]> streamBuffers){
        this.videoProviders = videoProviders;
        this.inBuffer = inBuffer;
        this.streamBuffers = streamBuffers;
    }


    private void handleVideoControl(UDPVideoControlPacket videoControlPacket){

        if (videoControlPacket.getProtocol() == EDGE_VIDEO_PROTOCOL.VIDEO_REQUEST){
            this.handleVideoControlRequest(videoControlPacket);
        }

        UDPVideoControlPacket udpVideoControlPacket = (UDPVideoControlPacket) videoControlPacket;
        TCPVideoControlPacket tcpVideoControlPacket = new TCPVideoControlPacket(
            OVERLAY_VIDEO_PROTOCOL.valueOf(udpVideoControlPacket.getProtocol().name()),
            udpVideoControlPacket.getVideo());

        tcpVideoControlPacket.setReceiverIP(udpVideoControlPacket.getReceiverIP());
        tcpVideoControlPacket.setReceiverPort(udpVideoControlPacket.getReceiverPort());
        tcpVideoControlPacket.setSenderIP(udpVideoControlPacket.getSenderIP());
        tcpVideoControlPacket.setSenderPort(udpVideoControlPacket.getSenderPort());

        this.inBuffer.push(tcpVideoControlPacket);
    }


    private void handleVideoControlRequest(UDPVideoControlPacket videoControlPacket){

        String client = videoControlPacket.getSenderIP();
        this.streamBuffers.addBoundedBuffer(client);

        BoundedBuffer<byte[]> streamBuffer = this.streamBuffers.getBoundedBuffer(client);
        NodeStreamWorker streamWorker = new NodeStreamWorker(client,streamBuffer);

        new Thread(streamWorker).start();
    }


    private void handleVideoList(UDPVideoListPacket videoListPacket){

        try{

            String clientIP = videoListPacket.getSenderIP();
            int clientPort = videoListPacket.getSenderPort();

            List<String> videos = this.videoProviders.getVideos().stream().collect(Collectors.toList());
            UDPVideoListPacket response = new UDPVideoListPacket(videos);

            UDPCarrier udpCarrier = new UDPCarrier();
            InetSocketAddress socketAddress = new InetSocketAddress(clientIP,clientPort);

            System.out.println("StreamWaitClient send: " + response);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(response);
            udpCarrier.disconnect();
            udpCarrier.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void run(){

        try{

            UDPPacket udpPacket;
            Map<UDP_TYPE,Consumer<UDPPacket>> handlers = new HashMap<>();
            InetSocketAddress socketAddress = new InetSocketAddress(CLIENT_ESTABLISH_CONNECTION_PORT);
            UDPCarrier udpCarrier = new UDPCarrier(socketAddress);

            handlers.put(UDP_TYPE.VIDEO_CONTROL, packet -> this.handleVideoControl((UDPVideoControlPacket)packet));
            handlers.put(UDP_TYPE.VIDEO_LIST, packet -> this.handleVideoList((UDPVideoListPacket)packet));

            System.out.println("StreamWaitClient service started");

            while (udpCarrier.isClosed() == false){

                if ((udpPacket = udpCarrier.receive()) != null){

                    System.out.println("StreamWaitClient receive: " + udpPacket);

                    if (handlers.containsKey(udpPacket.getType())){
                        handlers.get(udpPacket.getType()).accept(udpPacket);
                    }

                    else System.out.println("NodeStreamWaitClient unknown packet: " + udpPacket);
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }    
    }
}