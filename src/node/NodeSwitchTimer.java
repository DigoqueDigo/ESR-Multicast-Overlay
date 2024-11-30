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

    public static final Long DELAY = 2_000L;
    public static final Long PERIOD = 10_000L;
    private static final Long TOLERANCE = 500L;

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

            this.videoCurrentProviders.lock();
            Set<String> videos = this.videoCurrentProviders.keySet();

            for (String video : videos){

                String bestProvider = this.videoProviders.getBestProvider(video);
                String currentProvider = this.videoCurrentProviders.get(video);

                Long bestProviderTime = this.videoProviders.getProviderTime(video,bestProvider);
                Long currentProviderTime = this.videoProviders.getProviderTime(video,currentProvider);

                if (bestProviderTime != null){

                    if (currentProviderTime == null || currentProviderTime - bestProviderTime > TOLERANCE){

                        TCPVideoControlPacket videoCancelPacket = new TCPVideoControlPacket(OVERLAY_VIDEO_PROTOCOL.VIDEO_CANCEL,video);
                        TCPVideoControlPacket videoRequestPacket = new TCPVideoControlPacket(OVERLAY_VIDEO_PROTOCOL.VIDEO_REQUEST,video);

                        this.outBuffers.put(currentProvider,videoCancelPacket);
                        this.outBuffers.put(bestProvider,videoRequestPacket);
                        this.videoCurrentProviders.put(video,bestProvider);

                        System.out.println("NodeSwitchTimer send VIDEO CANCEL: " + video + " -> " + currentProvider);
                        System.out.println("NodeSwitchTimer send VIDEO REQUEST: " + video + " -> " + bestProvider);
                        System.out.println(this.videoCurrentProviders);
                    }
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }

        finally{
            this.videoCurrentProviders.unlock();
        }
    }
}