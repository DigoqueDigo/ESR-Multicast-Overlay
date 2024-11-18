package node.stream;
import java.io.IOException;
import struct.BoundedBuffer;
import utils.IO;


public class StreamWorker implements Runnable{

    private String clientIP;
    private BoundedBuffer<byte[]> videoBuffer;


    public StreamWorker(String clientIP, BoundedBuffer<byte[]> videoBuffer){
        this.clientIP = clientIP;
        this.videoBuffer = videoBuffer;
    }


    public void run(){

        try{

            String fifoName = this.clientIP;

            if (IO.mkfifo(fifoName) != 0){
                throw new IOException("StreamWorker can not create fifo: " + fifoName);
            } else System.out.println("StreamWorker create fifo: " + fifoName);

            Thread writer = new Thread(new StreamWriterWorker(this.videoBuffer,fifoName));
            Thread reader = new Thread(new StreamVlcjWorker(this.clientIP,fifoName));

            reader.start();

            // o vlc tem de ser o primeiro a abrir o fifo
            // isto nao assegura nada mas da algumas garantias
            Thread.sleep(1000);

            writer.start();

            reader.join();
            writer.join();

            if (IO.rm(fifoName) != 0){
                throw new IOException("Can not remove fifo: " + fifoName);
            } else System.out.println("StreamWorker remove fifo: " + fifoName);
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}