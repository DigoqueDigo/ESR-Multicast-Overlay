package service.core.control;
import java.util.List;
import java.util.TimerTask;
import packet.tcp.TCPPacket;
import packet.tcp.TCPFloodControlPacket;
import service.core.struct.MapBoundedBuffer;
import utils.IO;


public class ControlFloodTimer extends TimerTask{

    public static final int delay = 1000;
    public static final int period = 10000;

    private final String serverName;
    private final String signature;
    private final String videoFolder; 
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public ControlFloodTimer(String serverName, String videoFolder, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.serverName = serverName;
        this.signature = serverName;
        this.videoFolder = videoFolder;
        this.outBuffers = outBuffers;
    }


    public void run(){

        System.out.println("ControlFloodTimer repeat execution");

        List<String> videos = IO.listFiles(this.videoFolder);
        TCPFloodControlPacket tcpFloodPacket = new TCPFloodControlPacket(this.serverName,videos);
        tcpFloodPacket.addSignature(this.signature);

        for (String neighbour : this.outBuffers.getKeys()){
            this.outBuffers.put(neighbour,tcpFloodPacket);
            System.out.println("ControlFloodTimer send: " + this.signature + " -> " + neighbour);
        }
    }
}