package service.establishconnection;
import java.net.ServerSocket;
import java.net.Socket;
import packet.tcp.TCPPacket;
import service.core.connection.ConnectionWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.Buffers;


public class WaitEstablishConnection implements Runnable{

    public static final int ESTABLISH_CONNECTION_PORT = 3000; 

    private Buffers buffers;


    public WaitEstablishConnection(Buffers buffers){
        this.buffers = buffers;
    }


    public void run(){

        try{

            Socket socket;
            ServerSocket serverSocket = new ServerSocket(ESTABLISH_CONNECTION_PORT);

            while ((socket = serverSocket.accept()) != null){

                String neighbour = socket.getInetAddress().getHostAddress();
                this.buffers.addOutBuffer(neighbour);

                BoundedBuffer<TCPPacket> inBuffer = this.buffers.getInBuffer();
                BoundedBuffer<TCPPacket> outBuffer = this.buffers.getOutBuffer(neighbour);
                ConnectionWorker connectionWorker = new ConnectionWorker(socket,inBuffer,outBuffer);

                new Thread(connectionWorker).start();
            }

            serverSocket.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}