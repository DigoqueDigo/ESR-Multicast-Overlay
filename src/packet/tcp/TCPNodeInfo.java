package packet.tcp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class TCPNodeInfo extends TCPPacket{

    private String node;
    private JSONObject jsonObject;


    public TCPNodeInfo(){
        super(TYPE.NODEINFO);
    }


    public TCPNodeInfo(String node){
        super(TYPE.NODEINFO);
        this.node = node;
        this.jsonObject = new JSONObject();
    }


    public TCPNodeInfo(String node, JSONObject jsonObject){
        super(TYPE.NODEINFO);
        this.node = node;
        this.jsonObject = jsonObject;
    }


    public String getNode(){
        return this.node;
    }


    public JSONObject getJsonObject(){
        return this.jsonObject;
    }


    public void setJsonObject(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }


    public byte[] serialize(){

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPNodeInfo.class);
        kryo.writeObject(output,this.node);
        String jsonString = jsonObject.toString();
        kryo.writeObject(output,jsonString);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPNodeInfo deserialize(byte[] data){

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPNodeInfo.class);
        String node = kryo.readObject(input,String.class);
        String jsonString = kryo.readObject(input,String.class);
        JSONObject jsonObject = new JSONObject(jsonString);
        TCPNodeInfo udp_packet = new TCPNodeInfo(node,jsonObject);
        input.close();

        return udp_packet;
    }
}