package node;
import java.util.TimerTask;
import struct.VideoProviders;


public class NodeZombieTimer extends TimerTask{

    public static final int DELAY = 1_000;
    public static final int PERIOD = 10_000;

    private VideoProviders videoProviders;


    public NodeZombieTimer(VideoProviders videoProviders){
        this.videoProviders = videoProviders;
    }


    public void run(){
        System.out.println("NodeZombieTimer repeat execution");
        this.videoProviders.deleteZombies();
        System.out.println(this.videoProviders);
    }
}