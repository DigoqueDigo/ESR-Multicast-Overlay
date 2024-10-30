package service.core.connection;
import java.io.InputStream;
import carrier.TCPCarrier;
import packet.tcp.TCPPacket;
import service.core.struct.BoundedBuffer;


public class ConnectionReaderWorker implements Runnable{

    private String interfaceIP;
    private InputStream inputStream;
    private BoundedBuffer<TCPPacket> inBuffer;


    public ConnectionReaderWorker(String interfaceIP, InputStream inputStream, BoundedBuffer<TCPPacket> inBuffer){
        this.interfaceIP = interfaceIP;
        this.inputStream = inputStream;
        this.inBuffer = inBuffer;
    }


    public void run(){

        try{

            TCPPacket receivePacket;
            TCPCarrier tcpCarrier = new TCPCarrier(this.inputStream,null);

            while ((receivePacket = tcpCarrier.receive()) != null){
                receivePacket.setReceiver(interfaceIP);
                this.inBuffer.push(receivePacket);
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}