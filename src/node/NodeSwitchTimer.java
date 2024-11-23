package node;
import java.util.Set;
import java.util.TimerTask;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
import packet.tcp.TCPVideoControlPacket.OVERLAY_VIDEO_PROTOCOL;
import struct.MapBoundedBuffer;
import struct.VideoCurrentProviders;
import struct.VideoProviders;


public class NodeSwitchTimer extends TimerTask{

    public static final int DELAY = 2_000;
    public static final int PERIOD = 10_000;

    private VideoProviders videoProviders;
    private VideoCurrentProviders videoCurrentProviders;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public NodeSwitchTimer(VideoProviders videoProviders, VideoCurrentProviders videoCurrentProviders, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.videoProviders = videoProviders;
        this.videoCurrentProviders = videoCurrentProviders;
        this.outBuffers = outBuffers;
    }


    public void run(){

        try{

            System.out.println("NodeSwitchTimer update providers");

            this.videoCurrentProviders.lock();
            Set<String> videos = this.videoCurrentProviders.keySet();

            for (String video : videos){

                String bestProvider = this.videoProviders.getBestProvider(video);
                String currentProvider = this.videoCurrentProviders.get(video);

                if (bestProvider != null && !currentProvider.equals(bestProvider)){

                    TCPVideoControlPacket videoCancelPacket = new TCPVideoControlPacket(OVERLAY_VIDEO_PROTOCOL.VIDEO_CANCEL,video);
                    TCPVideoControlPacket videoRequestPacket = new TCPVideoControlPacket(OVERLAY_VIDEO_PROTOCOL.VIDEO_REQUEST,video);

                    this.outBuffers.put(currentProvider,videoCancelPacket);
                    this.outBuffers.put(bestProvider,videoRequestPacket);
                    this.videoCurrentProviders.put(video,bestProvider);

                    System.out.println("NodeSwitchTimer replace: " + currentProvider + " -> " + bestProvider);
                }
            }

            System.out.println("NodeSwitchTimer finished");
        }

        catch (Exception e){
            e.printStackTrace();
        }

        finally{
            this.videoCurrentProviders.unlock();
        }
    }
}