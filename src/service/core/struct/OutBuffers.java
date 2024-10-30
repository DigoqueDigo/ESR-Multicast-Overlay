package service.core.struct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import packet.tcp.TCPPacket;


public class OutBuffers{

    private static final int BUFFER_SIZE = 10;
    private ConcurrentMap<String,BoundedBuffer<TCPPacket>> outBuffers;


    public OutBuffers(){
        this.outBuffers = new ConcurrentHashMap<String,BoundedBuffer<TCPPacket>>();
    }


    public BoundedBuffer<TCPPacket> getOutBuffer(String key){
        return this.outBuffers.get(key);
    }


    public void addOutBuffer(String key){
        this.outBuffers.putIfAbsent(key,new BoundedBuffer<TCPPacket>(BUFFER_SIZE));
    }


    public void sendToAll(TCPPacket tcpPacket){
        this.outBuffers.values().stream().forEach(buffer -> {
            try {buffer.push(tcpPacket);}
            catch (Exception e) {}
        });
    }
}