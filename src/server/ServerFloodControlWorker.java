package server;
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


    private void handleConnectionStatePacket(TCPConnectionStatePacket connectionStatePacket){
        if (connectionStatePacket.getProtocol() == CONNECTION_STATE_PROTOCOL.CONNECTION_LOST){
            this.handleConnectionLost(connectionStatePacket);
        }
    }


    private void handleConnectionLost(TCPConnectionStatePacket connectionStatePacket){
        String neighbour = connectionStatePacket.getSender();
        this.outBuffers.removeBoundedBuffer(neighbour);
    }


    public void run(){

        try{

            TCPPacket tcpPacket;

            while ((tcpPacket = this.controlBuffer.pop()) != null){
                if (tcpPacket.getType() == TCP_TYPE.CONNECTION_STATE){
                    this.handleConnectionStatePacket((TCPConnectionStatePacket) tcpPacket);
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}