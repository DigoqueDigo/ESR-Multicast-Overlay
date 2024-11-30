package client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketException;
import carrier.UDPCarrier;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPVideoControlPacket.EDGE_VIDEO_PROTOCOL;


public class ClientStream{

    private final String video;
    private final String edgeNodeIP;
    private final int edgeNodeConnectionPort;
    private final int edgeNodeStreamPort;


    public ClientStream(String video, String edgeNodeIP, int edgeNodeConnectionPort, int edgeNodeStreamPort){
        this.video = video;
        this.edgeNodeIP = edgeNodeIP;
        this.edgeNodeConnectionPort = edgeNodeConnectionPort;
        this.edgeNodeStreamPort = edgeNodeStreamPort;
    }


    public void start(){

        try{
 
            UDPCarrier udpCarrier = new UDPCarrier();
            InetSocketAddress socketAddress = new InetSocketAddress(this.edgeNodeIP,this.edgeNodeConnectionPort);

            UDPVideoControlPacket videoRequest = new UDPVideoControlPacket(EDGE_VIDEO_PROTOCOL.VIDEO_REQUEST,this.video);
            UDPVideoControlPacket videoCancel = new UDPVideoControlPacket(EDGE_VIDEO_PROTOCOL.VIDEO_CANCEL,this.video);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoRequest);
            udpCarrier.disconnect();

            String link = "udp://{ADDRESS}:{PORT}";
            link = link.replace("{ADDRESS}","0.0.0.0");
            link = link.replace("{PORT}",Integer.toString(this.edgeNodeStreamPort));

            ProcessBuilder ffplayProcessBuilder = new ProcessBuilder(
                "ffplay",
                "-vf",
                "scale=640:-1",
                link
            );

            Process ffplay = ffplayProcessBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(ffplay.getErrorStream()))){
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int ffplayExitCode = ffplay.waitFor();
            System.out.println("ClientStream ffplay exit code: " + ffplayExitCode);
            System.out.println("ClientStream send packet: " + videoCancel);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoCancel);
            udpCarrier.disconnect();
            udpCarrier.close();
        }

        catch (SocketException e){
            System.out.println("ClientStream can not contact: " + this.edgeNodeIP);
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}