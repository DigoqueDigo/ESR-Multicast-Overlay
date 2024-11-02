package service.establishconnection;
import java.io.IOException;
import java.net.Socket;
import packet.tcp.TCPPacket;
import service.core.connection.ConnectionWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;


public class FloodEstablishConnection implements Runnable{

    private OutBuffers outBuffers;
    private BoundedBuffer<TCPPacket> inBuffer;
    private BoundedBuffer<String> connectionBuffer;


    public FloodEstablishConnection(BoundedBuffer<String> connectionBuffer, BoundedBuffer<TCPPacket> inBuffer, OutBuffers outBuffers){
        this.connectionBuffer = connectionBuffer;
        this.outBuffers = outBuffers;
        this.inBuffer = inBuffer;
    }


    public void run(){

        try{

            String neighbour;
            System.out.println("FloodEstablishConnection service started");

            while ((neighbour = this.connectionBuffer.pop()) != null){

                try{

                    Socket socket = new Socket(neighbour,WaitEstablishConnection.ESTABLISH_CONNECTION_PORT);
                    String myInterface = socket.getLocalAddress().getHostAddress();
                    this.outBuffers.addOutBuffer(neighbour);

                    BoundedBuffer<TCPPacket> outBuffer = this.outBuffers.getOutBuffer(neighbour);
                    ConnectionWorker connectionWorker = new ConnectionWorker(socket,inBuffer,outBuffer);

                    new Thread(connectionWorker).start();
                    System.out.println("FloodEstablishConnection service concat: " + myInterface + " -> " + neighbour);
                }

                catch (IOException e){
                    System.out.println("FloodEstablishConnection service can not contact: " + neighbour);
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}