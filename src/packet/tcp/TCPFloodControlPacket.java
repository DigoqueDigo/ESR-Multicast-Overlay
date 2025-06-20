package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TCPFloodControlPacket extends TCPPacket{

    private String serverName;
    private long timestamp;
    private List<String> signatures;
    private List<String> videos;


    public TCPFloodControlPacket(){
        super(TCP_TYPE.CONTROL_FLOOD);
    }


    public TCPFloodControlPacket(String serverName, List<String> videos) {
        super(TCP_TYPE.CONTROL_FLOOD);
        this.serverName = serverName;
        this.timestamp = System.currentTimeMillis();
        this.signatures = new ArrayList<>();
        this.videos = videos;
    }


    public TCPFloodControlPacket(TCPFloodControlPacket packet) {
        super(packet.getType(),
            packet.getReceiverIP(),
            packet.getReceiverPort(),
            packet.getSenderIP(),
            packet.getSenderPort());
        this.serverName = packet.getServerName();
        this.timestamp = packet.getTimestamp();
        this.signatures = packet.getSignatures();
        this.videos = packet.getVideos();
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


    public List<String> getVideos() {
        return this.videos.stream().collect(Collectors.toList());
    }


    public void addSignature(String signature) {
        this.signatures.add(signature);
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tServerName: " + this.serverName);
        buffer.append("\tTimestamp: " + this.timestamp);
        buffer.append("\tSignatures: " + this.signatures);
        buffer.append("\tVideos: " + this.videos);
        return buffer.toString();
    }


    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(ArrayList.class);
        kryo.register(TCPPacket.TCP_TYPE.class);
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
        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(TCPFloodControlPacket.class);

        TCPFloodControlPacket packet = kryo.readObject(input,TCPFloodControlPacket.class);
        input.close();

        return packet;
    }
}