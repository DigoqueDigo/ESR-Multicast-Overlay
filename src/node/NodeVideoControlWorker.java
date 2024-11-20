package node;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
import packet.tcp.TCPPacket.TCP_TYPE;
import packet.tcp.TCPVideoControlPacket.OVERLAY_VIDEO_PROTOCOL;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;
import struct.VideoConsumers;
import struct.VideoProviders;


public class NodeVideoControlWorker implements Runnable{

    private VideoProviders videoProviders;
    private VideoConsumers videoConsumers;
    private BoundedBuffer<TCPPacket> videoBuffer;
    private MapBoundedBuffer<String,byte[]> streamBuffers;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public NodeVideoControlWorker(VideoProviders videoProviders, VideoConsumers videoConsumers, BoundedBuffer<TCPPacket> videoBuffer, MapBoundedBuffer<String,byte[]> streaBuffers, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.videoProviders = videoProviders;
        this.videoConsumers = videoConsumers;
        this.videoBuffer = videoBuffer;
        this.streamBuffers = streaBuffers;
        this.outBuffers = outBuffers;
    }


    private void handleControlVideoRequest(TCPVideoControlPacket videoControlPacket){

        String consumer = videoControlPacket.getSenderIP();
        String video = videoControlPacket.getVideo();

        // guardar os gajos interessado no video
        this.videoConsumers.put(video,consumer);

        // encaminhar o request para o melhor provider do video
        String bestProvider = this.videoProviders.getBestProvider(video);
        this.outBuffers.put(bestProvider,videoControlPacket);
    }


    private void handleControlVideoCancel(TCPVideoControlPacket videoControlPacket){

        String consumer = videoControlPacket.getSenderIP(); 
        String video = videoControlPacket.getVideo();

        // o consumer nao esta interessado no video
        this.videoConsumers.remove(video,consumer);

        // se deixou de haver consumers do video, encaminhar o cancel para o provider
        if (this.videoConsumers.containsKey(video) == false){
            String provider = this.videoProviders.getBestProvider(video);
            this.outBuffers.put(provider,videoControlPacket);
        }

        // se e um cliente, entao nao esta nos outbuffers
        // se o consumer for um client, tenho de parar a stream
        if (this.outBuffers.containsKey(consumer) == false){
            this.streamBuffers.put(consumer,new byte[0]);
            this.streamBuffers.removeBoundedBuffer(consumer);
        }
    }


    private void handleControlVideoReply(TCPVideoControlPacket videoControlPacket){

        String video = videoControlPacket.getVideo();
        Set<String> consumers = this.videoConsumers.getConsumers(video);

        // iterar sobre quem esta interessado no video
        for (String consumer : consumers){

            // encaminhar o reply para um nodo do overlay
            if (this.outBuffers.containsKey(consumer)){
                this.outBuffers.put(consumer,videoControlPacket);
            }

            // esperatar os dados na stream do cliente
            else this.streamBuffers.put(consumer,videoControlPacket.getData());
        }
    }


    public void run(){

        TCPPacket tcpPacket;
        Map<TCP_TYPE,Consumer<TCPPacket>> handlers = new HashMap<>();
        Map<OVERLAY_VIDEO_PROTOCOL,Consumer<TCPVideoControlPacket>> videoHandlers = new HashMap<>();

        videoHandlers.put(OVERLAY_VIDEO_PROTOCOL.VIDEO_REQUEST, packet -> this.handleControlVideoRequest(packet));
        videoHandlers.put(OVERLAY_VIDEO_PROTOCOL.VIDEO_REPLY, packet -> this.handleControlVideoReply(packet));
        videoHandlers.put(OVERLAY_VIDEO_PROTOCOL.VIDEO_CANCEL, packet -> this.handleControlVideoCancel(packet));

        handlers.put(TCP_TYPE.CONTROL_VIDEO, packet -> {
            TCPVideoControlPacket videoControlPacket = (TCPVideoControlPacket) packet;
            videoHandlers.get(videoControlPacket.getProtocol()).accept(videoControlPacket);
        });

        while ((tcpPacket = this.videoBuffer.pop()) != null){

            if (handlers.containsKey(tcpPacket.getType())){
                handlers.get(tcpPacket.getType()).accept(tcpPacket);
            }

            else System.out.println("NodeVideoControlWorker unknown packet: " + tcpPacket);
        }
    }
}