package node.stream;
import java.io.IOException;
import struct.BoundedBuffer;
import utils.IO;


public class NodeStreamWorker implements Runnable{

    private String clientIP;
    private BoundedBuffer<byte[]> streamBuffer;


    public NodeStreamWorker(String clientIP, BoundedBuffer<byte[]> streamBuffer){
        this.clientIP = clientIP;
        this.streamBuffer = streamBuffer;
    }


    public void run(){

        try{

            String fifoName = this.clientIP;

            if (IO.mkfifo(fifoName) != 0){
                throw new IOException("StreamWorker can not create fifo: " + fifoName);
            }

            else System.out.println("StreamWorker create fifo: " + fifoName);

            Thread writer = new Thread(new NodeStreamWriterWorker(this.streamBuffer,fifoName));
            Thread reader = new Thread(new NodeStreamVlcjWorker(this.clientIP,fifoName));

            reader.start();

            // isto nao garante nada, mas o vlc tem de ser o primeiro a abrir
            Thread.sleep(1000);

            writer.start();

            reader.join();
            writer.join();

            if (IO.rm(fifoName) != 0){
                throw new IOException("Can not remove fifo: " + fifoName);
            }

            else System.out.println("StreamWorker remove fifo: " + fifoName);
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}