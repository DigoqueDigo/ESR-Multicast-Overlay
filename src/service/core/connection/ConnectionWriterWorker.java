package service.core.connection;
import java.io.OutputStream;
import carrier.TCPCarrier;
import packet.tcp.TCPPacket;
import service.core.struct.BoundedBuffer;


public class ConnectionWriterWorker implements Runnable{

    private String interfaceIP;
    private OutputStream outputStream;
    private BoundedBuffer<TCPPacket> outBuffer;


    public ConnectionWriterWorker(String interfaceIP, OutputStream outputStream, BoundedBuffer<TCPPacket> outBuffer){
        this.interfaceIP = interfaceIP;
        this.outputStream = outputStream;
        this.outBuffer = outBuffer;
    }


    public void run(){

        try{

            TCPPacket tcpPacket;
            TCPCarrier tcpCarrier = new TCPCarrier(null,this.outputStream);

            while ((tcpPacket = this.outBuffer.pop()) != null){
                tcpPacket.setSender(interfaceIP);
                tcpCarrier.send(tcpPacket);
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}