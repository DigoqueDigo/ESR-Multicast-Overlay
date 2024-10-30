package service.core.connection;
import java.io.IOException;
import java.net.Socket;
import packet.tcp.TCPPacket;
import service.core.struct.BoundedBuffer;


public class ConnectionWorker implements Runnable{

    private Socket socket;
    private BoundedBuffer<TCPPacket> inBuffer;
    private BoundedBuffer<TCPPacket> outBuffer;


    public ConnectionWorker(Socket socket, BoundedBuffer<TCPPacket> inBuffer, BoundedBuffer<TCPPacket> outBuffer) throws IOException{
        this.socket = socket;
        this.inBuffer = inBuffer;
        this.outBuffer = outBuffer;
    }


    public void run(){

        try{

            ConnectionReaderWorker connectionReaderWorker = new ConnectionReaderWorker(this.socket.getInputStream(),this.inBuffer);
            ConnectionWriterWorker connectionWriterWorker = new ConnectionWriterWorker(this.socket.getOutputStream(),this.outBuffer);

            Thread reader = new Thread(connectionReaderWorker);
            Thread writer = new Thread(connectionWriterWorker);

            reader.start();
            writer.start();

            reader.join();
            writer.join();

            socket.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}