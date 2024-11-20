package service.establishconnection;
import java.io.IOException;
import java.net.Socket;
import packet.tcp.TCPPacket;
import service.connection.ConnectionWorker;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;


public class FloodEstablishConnection implements Runnable{

    private BoundedBuffer<TCPPacket> inBuffer;
    private BoundedBuffer<String> connectionBuffer;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public FloodEstablishConnection(BoundedBuffer<String> connectionBuffer, BoundedBuffer<TCPPacket> inBuffer, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.connectionBuffer = connectionBuffer;
        this.outBuffers = outBuffers;
        this.inBuffer = inBuffer;
    }


    public void run(){

        String neighbour;
        System.out.println("FloodEstablishConnection service started");

        while ((neighbour = this.connectionBuffer.pop()) != null){

            try{

                if (this.outBuffers.containsKey(neighbour) == false){

                    Socket socket = new Socket(neighbour,WaitEstablishConnection.ESTABLISH_CONNECTION_PORT);
                    String myInterface = socket.getLocalAddress().getHostAddress();
                    this.outBuffers.addBoundedBuffer(neighbour);

                    BoundedBuffer<TCPPacket> outBuffer = this.outBuffers.getBoundedBuffer(neighbour);
                    ConnectionWorker connectionWorker = new ConnectionWorker(socket,inBuffer,outBuffer);

                    new Thread(connectionWorker).start();
                    System.out.println("FloodEstablishConnection service concat: " + myInterface + " -> " + neighbour);
                }
            }

            catch (IOException e){
                this.outBuffers.removeBoundedBuffer(neighbour);
                System.out.println("FloodEstablishConnection service can not contact: " + neighbour);
            }
        }
    }
}