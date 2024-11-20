package server;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import packet.tcp.TCPConnectionStatePacket;
import packet.tcp.TCPConnectionStatePacket.CONNECTION_STATE_PROTOCOL;
import packet.tcp.TCPVideoControlPacket.OVERLAY_VIDEO_PROTOCOL;
import packet.tcp.TCPPacket;
import packet.tcp.TCPPacket.TCP_TYPE;
import packet.tcp.TCPVideoControlPacket;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;


public class ServerVideoControlWorker implements Runnable{

    private String videoFolder;
    private Map<String,CopyOnWriteArrayList<String>> videoCatchers;
    private BoundedBuffer<TCPPacket> videoBuffer;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public ServerVideoControlWorker(String videoFolfer, BoundedBuffer<TCPPacket> videoBuffer, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.videoFolder = videoFolfer;
        this.videoCatchers = new HashMap<>();
        this.videoBuffer = videoBuffer;
        this.outBuffers = outBuffers;
    }


    private void handleControlVideoRequest(TCPVideoControlPacket videoControlPacket){

        String consumer = videoControlPacket.getSenderIP();
        String video = videoControlPacket.getVideo();
        boolean startCatcher = !this.videoCatchers.containsKey(video);

        this.videoCatchers.putIfAbsent(video,new CopyOnWriteArrayList<>());
        this.videoCatchers.get(video).add(consumer);

        if (startCatcher){
            CopyOnWriteArrayList<String> consumers = this.videoCatchers.get(video);
            ServerVideoCatcher serverVideoCatcher = new ServerVideoCatcher(video,videoFolder,consumers,outBuffers);
            new Thread(serverVideoCatcher).start();
        }
    }


    private void handleControlVideoCancel(TCPVideoControlPacket videoControlPacket){

        String consumer = videoControlPacket.getSenderIP();
        String video = videoControlPacket.getVideo();

        if (this.videoCatchers.containsKey(video)){

            CopyOnWriteArrayList<String> consumers = this.videoCatchers.get(video);
            consumers.remove(consumer);

            if (consumers.size() == 0){
                this.videoCatchers.remove(video);
            }
        }
    }


    private void handleConnectionLost(TCPConnectionStatePacket connectionStatePacket){

        String consumer = connectionStatePacket.getSenderIP();

        for (String video : this.videoCatchers.keySet()){

            CopyOnWriteArrayList<String> consumers = this.videoCatchers.get(video);
            consumers.remove(consumer);

            if (consumers.size() == 0){
                this.videoCatchers.remove(video);
            }
        }
    }


    public void run(){

        TCPPacket tcpPacket;
        Map<TCP_TYPE,Consumer<TCPPacket>> handlers = new HashMap<>();
        Map<OVERLAY_VIDEO_PROTOCOL,Consumer<TCPVideoControlPacket>> videoHandlers = new HashMap<>();
        Map<CONNECTION_STATE_PROTOCOL,Consumer<TCPConnectionStatePacket>> connectionStateHandlers = new HashMap<>();

        videoHandlers.put(OVERLAY_VIDEO_PROTOCOL.VIDEO_REQUEST, packet -> this.handleControlVideoRequest(packet));
        videoHandlers.put(OVERLAY_VIDEO_PROTOCOL.VIDEO_CANCEL, packet -> this.handleControlVideoCancel(packet));
        connectionStateHandlers.put(CONNECTION_STATE_PROTOCOL.CONNECTION_LOST, packet -> this.handleConnectionLost(packet));

        handlers.put(TCP_TYPE.CONTROL_VIDEO, packet -> {
            TCPVideoControlPacket videoControlPacket = (TCPVideoControlPacket) packet;
            videoHandlers.get(videoControlPacket.getProtocol()).accept(videoControlPacket);
        });

        handlers.put(TCP_TYPE.CONTROL_CONNECTION_STATE, packet -> {
            TCPConnectionStatePacket connectionStatePacket = (TCPConnectionStatePacket) packet;
            connectionStateHandlers.get(connectionStatePacket.getProtocol()).accept(connectionStatePacket);
        });

        while ((tcpPacket = this.videoBuffer.pop()) != null){

            if (handlers.containsKey(tcpPacket.getType())){
                handlers.get(tcpPacket.getType()).accept(tcpPacket);
            }

            else System.out.println("ServerVideoControlWorker unknow packet: " + tcpPacket);
        }
    }
}