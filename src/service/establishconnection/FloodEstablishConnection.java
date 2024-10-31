package service.establishconnection;
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

                this.outBuffers.addOutBuffer(neighbour);
                Socket socket = new Socket(neighbour,WaitEstablishConnection.ESTABLISH_CONNECTION_PORT);

                BoundedBuffer<TCPPacket> outBuffer = this.outBuffers.getOutBuffer(neighbour);
                ConnectionWorker connectionWorker = new ConnectionWorker(socket,inBuffer,outBuffer);

                connectionWorkers.add(new Thread(connectionWorker));
                System.out.println("FloodEstablishConnection with " + neighbour);
            }

            System.out.println("FloodEstablishConnection all neighbours were connected");
            for (Thread worker : connectionWorkers) {worker.start();}
            for (Thread worker : connectionWorkers) {worker.join();}
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}