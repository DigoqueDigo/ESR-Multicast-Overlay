package node.stream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import struct.BoundedBuffer;


public class NodeStreamWriterWorker implements Runnable{

    private String fifo;
    private BoundedBuffer<byte[]> streamBuffer;


    public NodeStreamWriterWorker(BoundedBuffer<byte[]> streamBuffer, String fifo) throws IOException{
        this.streamBuffer = streamBuffer;
        this.fifo = fifo;
    }


    public void run(){

        try{

            byte[] video_data;
            OutputStream outputStream = new FileOutputStream(this.fifo);
            System.out.println("NodeStreamWriterWorker open fifo: " + this.fifo);

            while ((video_data = this.streamBuffer.pop()) != null && video_data.length > 0){
                outputStream.write(video_data);
            }

            outputStream.close();
            System.out.println("NodeStreamWriterWorker close fifo: " + this.fifo);
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}