package service.establishconnection;
import java.net.ServerSocket;
import java.net.Socket;
import packet.tcp.TCPPacket;
import service.core.connection.ConnectionWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;


public class WaitEstablishConnection implements Runnable{

    public static final int ESTABLISH_CONNECTION_PORT = 3000; 

    private OutBuffers outBuffers;
    private BoundedBuffer<TCPPacket> inBuffer;


    public WaitEstablishConnection(BoundedBuffer<TCPPacket> inBuffer, OutBuffers outBuffers){
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
                this.outBuffers.addOutBuffer(neighbour);

                BoundedBuffer<TCPPacket> outBuffer = this.outBuffers.getOutBuffer(neighbour);
                ConnectionWorker connectionWorker = new ConnectionWorker(socket,inBuffer,outBuffer);

                new Thread(connectionWorker).start();
                System.out.println("Interface " + socket.getLocalAddress().getHostAddress() + ": WaitEstablishConnection with " + neighbour);
            }

            serverSocket.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}