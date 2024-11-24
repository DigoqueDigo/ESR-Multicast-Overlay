package service.core;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import packet.tcp.TCPPacket;
import packet.tcp.TCPPacket.TCP_TYPE;
import struct.BoundedBuffer;


public class CoreWorker implements Runnable{

    private BoundedBuffer<TCPPacket> inBuffer;
    private BoundedBuffer<TCPPacket> controlBuffer;
    private BoundedBuffer<TCPPacket> videoBuffer;


    public CoreWorker(BoundedBuffer<TCPPacket> inBuffer, BoundedBuffer<TCPPacket> controlBuffer, BoundedBuffer<TCPPacket> videoBuffer){
        this.inBuffer = inBuffer;
        this.controlBuffer = controlBuffer;
        this.videoBuffer = videoBuffer;
    }


    public void run(){

        TCPPacket tcpPacket;
        Map<TCP_TYPE,Consumer<TCPPacket>> handlers = new HashMap<>();

        handlers.put(TCP_TYPE.CONTROL_FLOOD, packet -> this.controlBuffer.push(packet));
        handlers.put(TCP_TYPE.CONTROL_GRANDFATHER, packet -> this.controlBuffer.push(packet));
        handlers.put(TCP_TYPE.CONTROL_VIDEO, packet -> this.videoBuffer.push(packet));
        handlers.put(TCP_TYPE.CONTROL_CONNECTION_STATE, packet -> {
            this.controlBuffer.push(packet);
            this.videoBuffer.push(packet);
        });

        while ((tcpPacket = this.inBuffer.pop()) != null){

        //    System.out.println("CoreWorker received packet: " + tcpPacket);

            if (handlers.containsKey(tcpPacket.getType())){
                handlers.get(tcpPacket.getType()).accept(tcpPacket);
            }

            else System.out.println("CoreWorker unknown packet: " + tcpPacket);
        }
    }
}