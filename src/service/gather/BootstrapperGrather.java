package service.gather;
import java.net.Socket;
import org.json.JSONObject;
import bootstrapper.Bootstrapper;
import carrier.TCPCarrier;
import packet.tcp.TCPBootstrapperPacket;


public class BootstrapperGrather{

    private String nodeName;
    private String bootstrapperIP;


    public BootstrapperGrather(String nodeName, String bootstrapperIP){
        this.nodeName = nodeName;
        this.bootstrapperIP = bootstrapperIP;
    }


    public JSONObject getBootstrapperInfo(){

        try{

            Socket socket = new Socket(bootstrapperIP,Bootstrapper.PORT);
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