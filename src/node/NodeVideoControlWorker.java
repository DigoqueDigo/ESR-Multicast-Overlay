package node;
import java.util.Set;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
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


    private void handleControlVideo(TCPVideoControlPacket videoControlPacket){
        switch (videoControlPacket.getProtocol()){
            case VIDEO_REQUEST:
                this.handleControlVideoRequest(videoControlPacket);
                break;
            case VIDEO_CANCEL:
                this.handleControlVideoCancel(videoControlPacket);
                break;
            case VIDEO_REPLY:
                this.handleControlVideoReply(videoControlPacket);
                break;
        }
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

        try{

            TCPPacket tcpPacket;

            while ((tcpPacket = videoBuffer.pop()) != null){

                switch (tcpPacket.getType()){

                    case CONTROL_VIDEO:
                        this.handleControlVideo((TCPVideoControlPacket) tcpPacket);
                        break;

                    default:
                        System.out.println("StreamControl unknown packet: " + tcpPacket);
                        break;
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}