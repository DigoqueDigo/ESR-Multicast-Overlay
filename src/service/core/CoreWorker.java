package service.core;
import packet.tcp.TCPPacket;
import packet.tcp.TCPPacket.TYPE;
import service.core.struct.BoundedBuffer;


public class CoreWorker implements Runnable{

    private BoundedBuffer<TCPPacket> inBuffer;
    private BoundedBuffer<TCPPacket> controlBuffer;


    public CoreWorker(BoundedBuffer<TCPPacket> inBuffer, BoundedBuffer<TCPPacket> controlBuffer){
        this.inBuffer = inBuffer;
        this.controlBuffer = controlBuffer;
    }


    public void run(){

        try{

            TCPPacket tcpPacket;

            while ((tcpPacket =this.inBuffer.pop()) != null){

                TYPE type = tcpPacket.getType();
                System.out.println("CoreWorker received packet: " + tcpPacket);

                if (type == TYPE.CONTROL_FLOOD || type == TYPE.CONTROL_GRANDFATHER){
                    this.controlBuffer.push(tcpPacket);
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}