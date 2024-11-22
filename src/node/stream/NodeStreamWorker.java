package node.stream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import struct.BoundedBuffer;


public class NodeStreamWorker implements Runnable{

    public static final int STREAMING_PORT = 7000;

    private final String clientIP;
    private BoundedBuffer<byte[]> streamBuffer;


    public NodeStreamWorker(String clientIP, BoundedBuffer<byte[]> streamBuffer){
        this.clientIP = clientIP;
        this.streamBuffer = streamBuffer;
    }


    public void run(){

        try{

            String link = "udp://{ADDRESS}:{PORT}";
            link = link.replace("{ADDRESS}",this.clientIP);
            link = link.replace("{PORT}",Integer.toString(STREAMING_PORT));

            ProcessBuilder ffmpegProcessBuilder = new ProcessBuilder(
                "ffmpeg",
                "-re",
                "-i", "-",
                "-f", "mpegts",
                link
            );

            Process ffmpeg = ffmpegProcessBuilder.start();
            OutputStream outputStream = new BufferedOutputStream(ffmpeg.getOutputStream());
            byte[] video_data;

            while ((video_data = this.streamBuffer.pop()) != null && video_data.length > 0){
                outputStream.write(video_data);
            }

            outputStream.flush();
            outputStream.close();

            int ffmpegExitCode = ffmpeg.waitFor();
            System.out.println("NodeStreamWorker ffmpeg exit code: " + ffmpegExitCode);
            System.out.println("NodeStreamWorker stream finished");
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}