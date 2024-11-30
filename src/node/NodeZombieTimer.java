package node;
import java.util.TimerTask;
import struct.VideoProviders;


public class NodeZombieTimer extends TimerTask{

    public static final Long DELAY = 1_000L;
    public static final Long PERIOD = 10_000L;

    private VideoProviders videoProviders;


    public NodeZombieTimer(VideoProviders videoProviders){
        this.videoProviders = videoProviders;
    }


    public void run(){
        this.videoProviders.deleteZombies();
    }
}