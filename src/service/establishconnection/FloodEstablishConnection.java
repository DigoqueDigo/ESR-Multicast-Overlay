package service.establishconnection;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import packet.tcp.TCPPacket;
import service.core.connection.ConnectionWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;


public class FloodEstablishConnection implements Runnable{

    private List<String> neighbours;
    private OutBuffers outBuffers;
    private BoundedBuffer<TCPPacket> inBuffer;


    public FloodEstablishConnection(BoundedBuffer<TCPPacket> inBuffer, OutBuffers outBuffers, List<String> neighbours){
        this.neighbours = neighbours;
        this.outBuffers = outBuffers;
        this.inBuffer = inBuffer;
    }


    public void run(){

        try{

            List<Thread> connectionWorkers = new ArrayList<>();
            System.out.println("FloodEstablishConnection service started");

            for (String neighbour : this.neighbours){

                try{

                    Socket socket = new Socket(neighbour,WaitEstablishConnection.ESTABLISH_CONNECTION_PORT);
                    String myInterface = socket.getLocalAddress().getHostAddress();
                    this.outBuffers.addOutBuffer(neighbour);

                    BoundedBuffer<TCPPacket> outBuffer = this.outBuffers.getOutBuffer(neighbour);
                    ConnectionWorker connectionWorker = new ConnectionWorker(socket,inBuffer,outBuffer);

                    connectionWorkers.add(new Thread(connectionWorker));
                    System.out.println("FloodEstablishConnection service concat: " + myInterface + " -> " + neighbour);
                }

                catch (IOException e){
                    System.out.println("FloodEstablishConnection service can not contact: " + neighbour);
                }
            }

            System.out.println("FloodEstablishConnection service finished");
            for (Thread worker : connectionWorkers) {worker.start();}
            for (Thread worker : connectionWorkers) {worker.join();}
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}