package service.connection;
import java.io.OutputStream;
import carrier.TCPCarrier;
import packet.tcp.TCPPacket;
import struct.BoundedBuffer;


public class ConnectionWriterWorker implements Runnable{

    private String myInterface;
    private OutputStream outputStream;
    private BoundedBuffer<TCPPacket> outBuffer;


    public ConnectionWriterWorker(String myInterface, OutputStream outputStream, BoundedBuffer<TCPPacket> outBuffer){
        this.myInterface = myInterface;
        this.outputStream = outputStream;
        this.outBuffer = outBuffer;
    }


    public void run(){

        try{

            TCPPacket tcpPacket;
            TCPCarrier tcpCarrier = new TCPCarrier(null,this.outputStream);

            while ((tcpPacket = this.outBuffer.pop()) != null){
                tcpPacket = tcpPacket.clone();
                tcpPacket.setSenderIP(this.myInterface);
                tcpCarrier.send(tcpPacket);
            }
        }

        catch (Exception e){
            e.printStackTrace();   
        }
    }
}