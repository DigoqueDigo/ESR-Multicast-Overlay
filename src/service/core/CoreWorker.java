package service.core;
import packet.tcp.TCPPacket;
import packet.tcp.TCPPacket.TCP_TYPE;
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

                TCP_TYPE type = tcpPacket.getType();
                System.out.println("CoreWorker received packet:\n" + tcpPacket);

                switch (type){

                    case CONTROL_FLOOD:
                        this.controlBuffer.push(tcpPacket);
                        break;

                    case CONTROL_GRANDFATHER:
                        this.controlBuffer.push(tcpPacket);
                        break;

                    case CONNECTION_STATE:
                        this.controlBuffer.push(tcpPacket);
                        break;

                    default:
                        System.out.println("CoreWorker unknown packet:\n" + tcpPacket);
                        break;
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}