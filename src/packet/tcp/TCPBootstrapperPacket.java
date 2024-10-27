package packet.tcp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;



public class TCPBootstrapperPacket extends TCPPacket{

    private String node;
    private JSONObject jsonObject;


    public TCPBootstrapperPacket(){
        super(TYPE.BOOTSTRAPPER);
    }


    public TCPBootstrapperPacket(String node){
        super(TYPE.BOOTSTRAPPER);
        this.node = node;
        this.jsonObject = new JSONObject();
    }


    public TCPBootstrapperPacket(String node, JSONObject jsonObject){
        super(TYPE.BOOTSTRAPPER);
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

        kryo.register(TCPBootstrapperPacket.class);
        kryo.writeObject(output,this.node);
        String jsonString = jsonObject.toString();
        kryo.writeObject(output,jsonString);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPBootstrapperPacket deserialize(byte[] data){

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPBootstrapperPacket.class);
        String node = kryo.readObject(input,String.class);
        String jsonString = kryo.readObject(input,String.class);
        JSONObject jsonObject = new JSONObject(jsonString);
        TCPBootstrapperPacket packet = new TCPBootstrapperPacket(node,jsonObject);
        input.close();

        return packet;
    }
}