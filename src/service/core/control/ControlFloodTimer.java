package service.core.control;
import java.util.TimerTask;
import packet.tcp.TCPPacket;
import packet.tcp.TCPFloodControlPacket;
import service.core.struct.MapBoundedBuffer;


public class ControlFloodTimer extends TimerTask{

    public static final int delay = 1000;
    public static final int period = 10000;

    private String serverName;
    private String signature;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public ControlFloodTimer(String serverName, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.serverName = serverName;
        this.signature = serverName;
        this.outBuffers = outBuffers;
    }


    public void run(){

        System.out.println("ControlFloodTimer repeat execution");

        TCPFloodControlPacket tcpFloodPacket = new TCPFloodControlPacket(this.serverName);
        tcpFloodPacket.addSignature(this.signature);

        for (String neighbour : this.outBuffers.getKeys()){
            this.outBuffers.put(neighbour,tcpFloodPacket);
            System.out.println("ControlFloodTimer send: " + this.signature + " -> " + neighbour);
        }
    }
}