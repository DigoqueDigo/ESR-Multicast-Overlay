package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TCPFloodControlPacket extends TCPPacket {

    private String serverName;
    private long timestamp;
    private List<String> signatures;


    public TCPFloodControlPacket() {}


    public TCPFloodControlPacket(String serverName) {
        super(TYPE.CONTROL_FLOOD);
        this.serverName = serverName;
        this.timestamp = System.nanoTime();
        this.signatures = new ArrayList<>();
    }


    public TCPFloodControlPacket(String serverName, long timestamp) {
        super(TYPE.CONTROL_FLOOD);
        this.serverName = serverName;
        this.timestamp = timestamp;
        this.signatures = new ArrayList<>();
    }


    public TCPFloodControlPacket(TCPFloodControlPacket packet) {
        super(packet.getType(), packet.getReceiver(), packet.getSender());
        this.serverName = packet.getServerName();
        this.timestamp = packet.getTimestamp();
        this.signatures = packet.getSignatures();
    }


    public TCPFloodControlPacket clone(){
        return new TCPFloodControlPacket(this);
    }


    public long getTimestamp() {
        return this.timestamp;
    }


    public String getServerName() {
        return this.serverName;
    }


    public List<String> getSignatures() {
        return this.signatures.stream().collect(Collectors.toList());
    }


    public void addSignature(String signature) {
        this.signatures.add(signature);
    }
    

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tServerName: ").append(this.serverName);
        buffer.append("\tTimestamp: ").append(this.timestamp);
        buffer.append("\tSignatures: ").append(this.signatures);
        return buffer.toString();
    }


    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(ArrayList.class);
        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPFloodControlPacket.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPFloodControlPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(ArrayList.class);
        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPFloodControlPacket.class);

        TCPFloodControlPacket packet = kryo.readObject(input,TCPFloodControlPacket.class);
        input.close();

        return packet;
    }
}