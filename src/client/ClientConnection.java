package client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import carrier.UDPCarrier;
import node.stream.NodeStreamWaitClient;
import node.stream.NodeStreamWorker;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPVideoControlPacket.EDGE_VIDEO_PROTOCOL;


public class ClientConnection{

    private String edgeNode;
    private String video;


    public ClientConnection(String edgeNode, String video){
        this.edgeNode = edgeNode;
        this.video = video; 
    }


    public void start(){

        try{

            System.out.println(edgeNode);
 
            UDPCarrier udpCarrier = new UDPCarrier();
            InetSocketAddress socketAddress = new InetSocketAddress(this.edgeNode,NodeStreamWaitClient.CLIENT_ESTABLISH_CONNECTION_PORT);

            UDPVideoControlPacket videoRequest = new UDPVideoControlPacket(EDGE_VIDEO_PROTOCOL.VIDEO_REQUEST,this.video);
            UDPVideoControlPacket videoCancel = new UDPVideoControlPacket(EDGE_VIDEO_PROTOCOL.VIDEO_CANCEL,this.video);

            System.out.println("ClientConnection send packet: " + videoRequest);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoRequest);
            udpCarrier.disconnect();

            String link = "udp://{ADDRESS}:{PORT}";
            link = link.replace("{ADDRESS}","0.0.0.0");
            link = link.replace("{PORT}",Integer.toString(NodeStreamWorker.STREAMING_PORT));

            ProcessBuilder ffplayProcessBuilder = new ProcessBuilder(
                "ffplay",
                "-vf",
                "scale=640:-1",
                link
            );

            Process ffplay = ffplayProcessBuilder.start();
            System.out.println("ClientConnection ffplay started: " + link);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(ffplay.getErrorStream()))){
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int ffplayExitCode = ffplay.waitFor();
            System.out.println("ClientConnection ffplay exit code: " + ffplayExitCode);

            System.out.println("ClientConnection send packet: " + videoCancel);

            udpCarrier.connect(socketAddress);
            udpCarrier.send(videoCancel);
            udpCarrier.disconnect();
            udpCarrier.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}