package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TCPGrandfatherControlPacket extends TCPPacket{

    public enum GRANDFATHER_PROTOCOL{
        GRANDFATHER_REQUEST,
        GRANDFATHER_REPLY
    }

    private GRANDFATHER_PROTOCOL protocol;
    private List<String> grandparents;
    private String video;


    public TCPGrandfatherControlPacket(){
        super(TCP_TYPE.CONTROL_GRANDFATHER);
    }


    public TCPGrandfatherControlPacket(GRANDFATHER_PROTOCOL protocol, String video) {
        super(TCP_TYPE.CONTROL_GRANDFATHER);
        this.protocol = protocol;
        this.video = video;
        this.grandparents = new ArrayList<>();
    }


    public TCPGrandfatherControlPacket(GRANDFATHER_PROTOCOL protocol, String video, List<String> grandparents) {
        super(TCP_TYPE.CONTROL_GRANDFATHER);
        this.protocol = protocol;
        this.video = video;
        this.grandparents = grandparents.stream().collect(Collectors.toList());
    }


    public TCPGrandfatherControlPacket(TCPGrandfatherControlPacket packet) {
        super(packet.getType(),
            packet.getReceiverIP(),
            packet.getReceiverPort(),
            packet.getSenderIP(),
            packet.getSenderPort());
        this.protocol = packet.getProtocol();
        this.video = packet.getVideo();
        this.grandparents = packet.getGrandparents();
    }


    public TCPGrandfatherControlPacket clone(){
        return new TCPGrandfatherControlPacket(this);
    }


    public GRANDFATHER_PROTOCOL getProtocol(){
        return this.protocol;
    }


    public String getVideo(){
        return this.video;
    }


    public List<String> getGrandparents(){
        return this.grandparents.stream().collect(Collectors.toList());
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tProtocol: " + this.protocol.name());
        buffer.append("\tVideo: " + this.video);
        buffer.append("\tGrandfather: " + this.grandparents);
        return buffer.toString();
    }


    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(ArrayList.class);
        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(TCPGrandfatherControlPacket.class);
        kryo.register(TCPGrandfatherControlPacket.GRANDFATHER_PROTOCOL.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPGrandfatherControlPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(ArrayList.class);
        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(TCPGrandfatherControlPacket.class);
        kryo.register(TCPGrandfatherControlPacket.GRANDFATHER_PROTOCOL.class);

        TCPGrandfatherControlPacket packet = kryo.readObject(input,TCPGrandfatherControlPacket.class);
        input.close();

        return packet;
    }
}