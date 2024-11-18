package service.connection;
import java.io.InputStream;
import carrier.TCPCarrier;
import packet.tcp.TCPConnectionStatePacket;
import packet.tcp.TCPPacket;
import packet.tcp.TCPConnectionStatePacket.CONNECTION_STATE_PROTOCOL;
import struct.BoundedBuffer;


public class ConnectionReaderWorker implements Runnable{

    private String myInterface;
    private String neighbourInterface;
    private InputStream inputStream;
    private BoundedBuffer<TCPPacket> inBuffer;


    public ConnectionReaderWorker(String myInterce, String neighbourInterface, InputStream inputStream, BoundedBuffer<TCPPacket> inBuffer){
        this.myInterface = myInterce;
        this.neighbourInterface = neighbourInterface;
        this.inputStream = inputStream;
        this.inBuffer = inBuffer;
    }


    public void run(){

        try{

            TCPPacket receivePacket;
            TCPCarrier tcpCarrier = new TCPCarrier(this.inputStream,null);

            while ((receivePacket = tcpCarrier.receive()) != null){
                receivePacket.setReceiver(myInterface);
                this.inBuffer.push(receivePacket);
            }
        }

        catch (Exception e){

            // informar que a conexão perdeu-se
            TCPConnectionStatePacket tcpStatePacket = new TCPConnectionStatePacket(
                CONNECTION_STATE_PROTOCOL.CONNECTION_LOST,
                this.myInterface,
                this.neighbourInterface);

            try {
                this.inBuffer.push(tcpStatePacket);
                System.out.println("ConnectionReaderWorker lost connection: " + this.myInterface + " -> " + this.neighbourInterface);
            }

            catch (Exception f){
                f.printStackTrace();
            }
        }
    }
}