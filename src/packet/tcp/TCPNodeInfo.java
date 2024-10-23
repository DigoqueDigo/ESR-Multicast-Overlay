package packet.tcp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class TCPNodeInfo{

    private String node;
    private JSONObject jsonObject;


    public TCPNodeInfo() {}


    public TCPNodeInfo(String node){
        this.node = node;
        this.jsonObject = new JSONObject();
    }


    public TCPNodeInfo(String node, JSONObject jsonObject){
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
        kryo.register(JSONObject.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPNodeInfo deserialize(byte[] data){

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPNodeInfo.class);
        kryo.register(JSONObject.class);

        TCPNodeInfo udp_packet = kryo.readObject(input,TCPNodeInfo.class);
        input.close();

        return udp_packet;
    }
}