package service.core.stream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import service.struct.BoundedBuffer;


public class StreamWriterWorker implements Runnable{

    private OutputStream fifo;
    private BoundedBuffer<byte[]> videoBuffer;


    public StreamWriterWorker(BoundedBuffer<byte[]> videobuffer, String fifo) throws IOException{
        this.videoBuffer = videobuffer;
        this.fifo = new FileOutputStream(fifo);
    }


    public void run(){

        try{

            byte[] video_data;

            // tenho de receber um array vazio para saber que cheguei ao fim
            while ((video_data = this.videoBuffer.pop()) != null && video_data.length > 0){
                this.fifo.write(video_data);
            }

            this.fifo.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}