package server;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
import packet.tcp.TCPVideoControlPacket.OVERLAY_VIDEO_PROTOCOL;
import struct.MapBoundedBuffer;
import utils.IO;


public class ServerVideoCatcher implements Runnable{

    private static final int CHUNK_MAX_SIZE = 65_536;
    private static final int CHUNK_MIN_SIZE = 32_768;

    private final String video;
    private final String videoFolder;
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

            System.out.println("------------------------------------------");
            int bytes_read;
            int offset = 0;
            int length = CHUNK_MAX_SIZE;

            byte[] data_read = new byte[CHUNK_MAX_SIZE];
            String videoPath = this.videoFolder + this.video;
            String fifoPath = UUID.randomUUID().toString();
            System.out.println("------------------------------------------FIFO ANTES CRIADO");
            IO.mkfifo(fifoPath);
            System.out.println("------------------------------------------FIFO CRIADO");

            ProcessBuilder ffmpegProcessBuilder = new ProcessBuilder(
                "ffmpeg",
                "-re",
                "-fflags", "+genpts",
                "-stream_loop", "-1",
                "-i", videoPath,
                "-c:v", "libx264",
                "-b:v", "256k",
                "-c:a", "aac",
                "-b:a", "64k",
                "-g", "30",
                "-preset", "ultrafast",
                "-f", "mpegts",
                "-"
            );
            System.out.println("------------------------------------------FIFO DEPOIS DO START");
            ffmpegProcessBuilder.redirectOutput(new File(fifoPath));
            System.out.println("------------------------------------------FIFO DEPOIS DO START");
            
            new Thread(() -> {
                try {
                    ffmpegProcessBuilder.start();
                }
                catch (Exception e) {e.printStackTrace();}
            }).start();

            System.out.println("------------------------------------------FIFO DEPOIS DO START");
            
            InputStream inputStream = new FileInputStream(fifoPath);
            System.out.println("ServerVideoCatch ffmpeg started: " + this.video);

            while ((bytes_read = inputStream.read(data_read,offset,length)) > -1){

                offset += bytes_read;
                length -= bytes_read;

                System.out.println("ServerVideoCatcher consumers size: " + this.consumers.size());
                System.out.println("ServerVideoCatcher offset: " + offset);

                if ((this.consumers.size() > 0 && offset > CHUNK_MIN_SIZE) || length == 0){

                    byte[] data = Arrays.copyOf(data_read,offset);
                    TCPVideoControlPacket videoControlPacket = new TCPVideoControlPacket(OVERLAY_VIDEO_PROTOCOL.VIDEO_REPLY,this.video,data);

                    for (String consumer : this.consumers){
                        System.out.println("ServerVideoCatcher before send packet to: " + consumer);
                        this.outBuffers.put(consumer,videoControlPacket);
                        System.out.println("ServerVideoCatcher send packet to: " + consumer);
                    }

                    offset = 0;
                    length = CHUNK_MAX_SIZE;
                }
            }

            System.out.println("ServerVideoCatch EXIT LOOP ----------------------");

            inputStream.close();
          //  int exitCode = ffmpeg.waitFor();
            //System.out.println("ServerVideoCatch ffmpeg exit code: " + exitCode + " :: " + this.video);
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}