package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class TCPFloodPacket extends TCPPacket {
    
    private String serverName;
    private long timestamp;


    public TCPFloodPacket() {}


    public TCPFloodPacket(String serverName) {
        super(TYPE.CONTROL_FLOOD);
        this.serverName = serverName;
        this.timestamp = System.nanoTime();
    }


    public TCPFloodPacket(String serverName, long timestamp) {
        super(TYPE.CONTROL_FLOOD);
        this.serverName = serverName;
        this.timestamp = timestamp;
    }


    public long getTimestamp() {
        return this.timestamp;
    }


    public String getServerName() {
        return this.serverName;
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tServerName: ").append(this.serverName);
        buffer.append("\tTimestamp: ").append(this.timestamp);
        return buffer.toString();
    }


    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPFloodPacket.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPFloodPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPFloodPacket.class);

        TCPFloodPacket packet = kryo.readObject(input,TCPFloodPacket.class);
        input.close();

        return packet;
    }
}