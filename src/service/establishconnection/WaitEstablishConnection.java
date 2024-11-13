package service.establishconnection;
import java.net.ServerSocket;
import java.net.Socket;
import packet.tcp.TCPPacket;
import service.core.connection.ConnectionWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.MapBoundedBuffer;


public class WaitEstablishConnection implements Runnable{

    public static final int ESTABLISH_CONNECTION_PORT = 3000; 

    private BoundedBuffer<TCPPacket> inBuffer;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public WaitEstablishConnection(BoundedBuffer<TCPPacket> inBuffer, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.inBuffer = inBuffer;
        this.outBuffers = outBuffers;
    }


    public void run(){

        try{

            Socket socket;
            ServerSocket serverSocket = new ServerSocket(ESTABLISH_CONNECTION_PORT);
            System.out.println("WaitEstablishConnection service started");

            while ((socket = serverSocket.accept()) != null){

                String neighbour = socket.getInetAddress().getHostAddress();
                String myInterface = socket.getLocalAddress().getHostAddress();
                this.outBuffers.addBoundedBuffer(neighbour);

                BoundedBuffer<TCPPacket> outBuffer = this.outBuffers.getBoundedBuffer(neighbour);
                ConnectionWorker connectionWorker = new ConnectionWorker(socket,inBuffer,outBuffer);

                new Thread(connectionWorker).start();
                System.out.println("WaitEstablishConnection service contact: " + myInterface + " -> " + neighbour);
            }

            serverSocket.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}