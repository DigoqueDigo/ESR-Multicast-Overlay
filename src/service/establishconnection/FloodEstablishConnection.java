package service.establishconnection;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import packet.tcp.TCPPacket;
import service.core.connection.ConnectionWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.Buffers;


public class FloodEstablishConnection implements Runnable{

    private Buffers buffers;
    private List<String> neighbours;


    public FloodEstablishConnection(Buffers buffers, List<String> neighbours){
        this.buffers = buffers;
        this.neighbours = neighbours;
    }


    public void run(){

        try{

            List<Thread> connectionWorkers = new ArrayList<>();

            for (String neighbour : this.neighbours){

                this.buffers.addOutBuffer(neighbour);
                Socket socket = new Socket(neighbour,WaitEstablishConnection.ESTABLISH_CONNECTION_PORT);

                BoundedBuffer<TCPPacket> inBuffer = this.buffers.getInBuffer();
                BoundedBuffer<TCPPacket> outBuffer = this.buffers.getOutBuffer(neighbour);

                ConnectionWorker connectionWorker = new ConnectionWorker(socket,inBuffer,outBuffer);
                connectionWorkers.add(new Thread(connectionWorker));
            }

            for (Thread worker : connectionWorkers) {worker.start();}
            for (Thread worker : connectionWorkers) {worker.join();}
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}