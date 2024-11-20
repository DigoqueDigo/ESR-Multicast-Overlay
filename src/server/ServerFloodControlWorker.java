package server;
import java.util.HashMap;
import java.util.function.Consumer;
import packet.tcp.TCPConnectionStatePacket;
import packet.tcp.TCPPacket;
import packet.tcp.TCPPacket.TCP_TYPE;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;
import packet.tcp.TCPConnectionStatePacket.CONNECTION_STATE_PROTOCOL;


public class ServerFloodControlWorker implements Runnable{


    private BoundedBuffer<TCPPacket> controlBuffer;
    MapBoundedBuffer<String,TCPPacket> outBuffers;


    public ServerFloodControlWorker(BoundedBuffer<TCPPacket> controlBuffer, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.controlBuffer = controlBuffer;
        this.outBuffers = outBuffers;
    }


    private void handleConnectionLost(TCPConnectionStatePacket connectionStatePacket){
        String neighbour = connectionStatePacket.getSenderIP();
        this.outBuffers.removeBoundedBuffer(neighbour);
    }


    public void run(){

        TCPPacket tcpPacket;
        HashMap<TCP_TYPE,Consumer<TCPPacket>> handlers = new HashMap<>();
        HashMap<CONNECTION_STATE_PROTOCOL,Consumer<TCPConnectionStatePacket>> connectionStateHandlers = new HashMap<>();

        connectionStateHandlers.put(CONNECTION_STATE_PROTOCOL.CONNECTION_LOST, packet -> this.handleConnectionLost(packet));
        handlers.put(TCP_TYPE.CONTROL_CONNECTION_STATE, packet -> {
            TCPConnectionStatePacket connectionStatePacket = (TCPConnectionStatePacket) packet;
            connectionStateHandlers.get(connectionStatePacket.getProtocol()).accept(connectionStatePacket);
        });

        while ((tcpPacket = this.controlBuffer.pop()) != null){

            if (handlers.containsKey(tcpPacket.getType())){
                handlers.get(tcpPacket.getType()).accept(tcpPacket);
            }

            else System.out.println("ServerFloodControlWorker unknown packet: " + tcpPacket);
        }
    }
}