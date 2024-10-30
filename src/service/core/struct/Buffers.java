package service.core.struct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import packet.tcp.TCPPacket;


public class Buffers{

    private static final int BUFFER_SIZE = 10;

    private BoundedBuffer<TCPPacket> inBuffer;
    private ConcurrentMap<String,BoundedBuffer<TCPPacket>> outBuffers;


    public Buffers(){
        this.inBuffer = new BoundedBuffer<TCPPacket>(BUFFER_SIZE);
        this.outBuffers = new ConcurrentHashMap<String,BoundedBuffer<TCPPacket>>();
    }


    public BoundedBuffer<TCPPacket> getInBuffer(){
        return this.inBuffer;
    }


    public BoundedBuffer<TCPPacket> getOutBuffer(String key){
        return this.outBuffers.get(key);
    }


    public void addOutBuffer(String key){
        this.outBuffers.putIfAbsent(key,new BoundedBuffer<TCPPacket>(BUFFER_SIZE));
    }
}