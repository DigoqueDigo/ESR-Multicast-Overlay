package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class TCPFloodPacket extends TCPPacket {
    private String serverName;
    //Sender is upstream node that sent this packet to this node
    private String sender;
    private long timestamp;

    public TCPFloodPacket(String serverName, String sender) {
        super(TYPE.CONTROL_FLOOD);
        this.serverName = serverName;
        this.sender = sender;
        this.timestamp = System.nanoTime();
    }

    public TCPFloodPacket(String serverName, String sender, long timestamp) {
        super(TYPE.CONTROL_FLOOD);
        this.serverName = serverName;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public byte[] serialize() {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPEstablishControlConnectionPacket.class);
        kryo.writeObject(output, this.serverName);
        kryo.writeObject(output,this.sender);
        kryo.writeObject(output,this.timestamp);
        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static TCPFloodPacket deserialize(byte[] data) {
        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPFloodPacket.class);
        String serverName = kryo.readObject(input, String.class);
        String sender = kryo.readObject(input,String.class);
        long timestamp = kryo.readObject(input, Long.class);
        TCPFloodPacket packet = new TCPFloodPacket(serverName,sender,timestamp);
        input.close();

        return packet;
    }
}
