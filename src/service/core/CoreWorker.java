package service.core;
import packet.tcp.TCPPacket;
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

        try{

            TCPPacket tcpPacket;

            while ((tcpPacket =this.inBuffer.pop()) != null){

                System.out.println("CoreWorker received packet: " + tcpPacket);

                switch (tcpPacket.getType()){

                    case CONTROL_FLOOD:
                        this.controlBuffer.push(tcpPacket);
                        break;

                    case CONTROL_GRANDFATHER:
                        this.controlBuffer.push(tcpPacket);
                        break;

                    case CONTROL_CONNECTION_STATE:
                        this.controlBuffer.push(tcpPacket);
                        break;

                    case CONTROL_VIDEO:
                        this.videoBuffer.push(tcpPacket);

                    default:
                        System.out.println("CoreWorker unknown packet: " + tcpPacket);
                        break;
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}