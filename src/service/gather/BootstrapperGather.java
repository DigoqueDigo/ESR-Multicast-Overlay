package service.gather;
import java.net.Socket;
import org.json.JSONObject;
import carrier.TCPCarrier;
import packet.tcp.TCPBootstrapperPacket;


public class BootstrapperGather {

    private String nodeName;
    private String bootstrapperIP;
    private int bootstrapperPort;


    public BootstrapperGather(String nodeName, String bootstrapperIP, int bootstrapperPort){
        this.nodeName = nodeName;
        this.bootstrapperIP = bootstrapperIP;
        this.bootstrapperPort = bootstrapperPort;
    }


    public JSONObject getBootstrapperInfo(){

        try{

            Socket socket = new Socket(bootstrapperIP,this.bootstrapperPort);
            TCPCarrier tcpCarrier = new TCPCarrier(socket.getInputStream(),socket.getOutputStream());

            TCPBootstrapperPacket request = new TCPBootstrapperPacket(nodeName);
            tcpCarrier.send(request);

            TCPBootstrapperPacket response = (TCPBootstrapperPacket) tcpCarrier.receive();
            socket.close();

            return response.getJsonObject();
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}