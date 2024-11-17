package packet.tcp;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class TCPBootstrapperPacket extends TCPPacket{

    private String node;
    private JSONObject jsonObject;


    public TCPBootstrapperPacket(){
        super(TCP_TYPE.BOOTSTRAPPER);
        this.node = null;
        this.jsonObject = null;
    }


    public TCPBootstrapperPacket(String node){
        super(TCP_TYPE.BOOTSTRAPPER);
        this.node = node;
        this.jsonObject = new JSONObject();
    }


    public TCPBootstrapperPacket(String node, JSONObject jsonObject){
        super(TCP_TYPE.BOOTSTRAPPER);
        this.node = node;
        this.jsonObject = jsonObject;
    }


    public TCPBootstrapperPacket clone(){
        return null;
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

        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(JSONArray.class);
        kryo.register(JSONObject.class);
        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(TCPBootstrapperPacket.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPBootstrapperPacket deserialize(byte[] data){

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(JSONArray.class);
        kryo.register(JSONObject.class);
        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(TCPBootstrapperPacket.class);

        TCPBootstrapperPacket packet = kryo.readObject(input,TCPBootstrapperPacket.class);
        input.close();

        return packet;
    }
}