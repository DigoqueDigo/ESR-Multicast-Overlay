package server;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
import packet.tcp.TCPVideoControlPacket.OVERLAY_VIDEO_PROTOCOL;
import struct.MapBoundedBuffer;


public class ServerVideoCatcher implements Runnable{

    private static final int CHUNK_SIZE = 32_768;

    private String video;
    private String videoFolder;
    private CopyOnWriteArrayList<String> consumers;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public ServerVideoCatcher(String video, String videoFolder, CopyOnWriteArrayList<String> consumers, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.video = video;
        this.videoFolder = videoFolder;
        this.consumers = consumers;
        this.outBuffers = outBuffers;
    }


    public void run(){

        try{

            int bytes_read;
            byte[] data_read = new byte[CHUNK_SIZE];

            String path = this.videoFolder + this.video;
            InputStream inputStream = new FileInputStream(path);

            while ((bytes_read = inputStream.read(data_read)) > 0 && this.consumers.size() > 0){

                byte[] data = Arrays.copyOf(data_read,bytes_read);
                TCPVideoControlPacket videoControlPacket = new TCPVideoControlPacket(OVERLAY_VIDEO_PROTOCOL.VIDEO_REPLY,this.video,data);

                for (String consumer : this.consumers){
                    this.outBuffers.put(consumer,videoControlPacket);
                }
            }

            inputStream.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}