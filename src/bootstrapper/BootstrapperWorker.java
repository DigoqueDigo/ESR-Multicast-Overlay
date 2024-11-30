package bootstrapper;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import carrier.TCPCarrier;
import packet.tcp.TCPBootstrapperPacket;


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
            JSONObject content;
            TCPBootstrapperPacket tcpBootstrapperPacket = (TCPBootstrapperPacket) this.tcpCarrier.receive();

            try{
                String nodeName = tcpBootstrapperPacket.getNode();
                content = this.jsonObject.getJSONObject(nodeName);
            }

            catch (JSONException e){
                content = null;
            }

            tcpBootstrapperPacket.setJsonObject(content);
            this.tcpCarrier.send(tcpBootstrapperPacket);
            this.socket.close();

            System.out.println("BoostrapperWorker receive message: " + tcpBootstrapperPacket.getNode());
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}