package service.core.connection;
import java.io.InputStream;
import carrier.TCPCarrier;
import packet.tcp.TCPPacket;
import service.core.struct.BoundedBuffer;


public class ConnectionReaderWorker implements Runnable{

    private InputStream inputStream;
    private BoundedBuffer<TCPPacket> inBuffer;


    public ConnectionReaderWorker(InputStream inputStream, BoundedBuffer<TCPPacket> inBuffer){
        this.inputStream = inputStream;
        this.inBuffer = inBuffer;
    }


    public void run(){

        try{

            TCPPacket receivePacket;
            TCPCarrier tcpCarrier = new TCPCarrier(this.inputStream,null);

            while ((receivePacket = tcpCarrier.receive()) != null){
                this.inBuffer.push(receivePacket);
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}