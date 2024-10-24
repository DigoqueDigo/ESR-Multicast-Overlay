package bootstrapper;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONObject;
import carrier.TCPCarrier;
import packet.tcp.TCPNodeInfo;


public class BootstrapperWorker implements Runnable{

    private Socket socket;
    private JSONObject jsonObject;
    private TCPCarrier tcpCarrier;


    public BootstrapperWorker(Socket socket, JSONObject jsonObject) throws IOException{
        this.socket = socket;
        this.jsonObject = jsonObject;
        this.tcpCarrier = new TCPCarrier(
            socket.getInputStream(),
            socket.getOutputStream());
    }


    public void run(){

        try{

            TCPNodeInfo tcpNodeInfo = (TCPNodeInfo) this.tcpCarrier.receive();
            JSONObject content = this.jsonObject.getJSONObject(tcpNodeInfo.getNode());

            tcpNodeInfo.setJsonObject(content);
            this.tcpCarrier.send(tcpNodeInfo);
            this.socket.close();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}