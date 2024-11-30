package server;
import java.util.List;
import java.util.TimerTask;
import packet.tcp.TCPPacket;
import struct.MapBoundedBuffer;
import packet.tcp.TCPFloodControlPacket;
import utils.IO;


public class ServerFloodTimer extends TimerTask{

    public static final Long DELAY = 1_000L;
    public static final Long PERIOD = 10_000L;

    private final String serverName;
    private final String signature;
    private final String videoFolder; 
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public ServerFloodTimer(String serverName, String videoFolder, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.serverName = serverName;
        this.signature = serverName;
        this.videoFolder = videoFolder;
        this.outBuffers = outBuffers;
    }


    public void run(){

        List<String> videos = IO.listFiles(this.videoFolder);
        TCPFloodControlPacket tcpFloodPacket = new TCPFloodControlPacket(this.serverName,videos);
        tcpFloodPacket.addSignature(this.signature);

        for (String neighbour : this.outBuffers.getKeys()){
            this.outBuffers.put(neighbour,tcpFloodPacket);
            System.out.println("ServerFloodTimer send FLOOD: " + this.signature + " -> " + neighbour);
        }
    }
}