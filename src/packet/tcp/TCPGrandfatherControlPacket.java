package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TCPGrandfatherControlPacket extends TCPPacket {

    public enum GF_PROTOCOL{
        GRANDFATHER_REQUEST,
        GRANDFATHER_REPLY
    }

    private GF_PROTOCOL protocol;
    private List<String> grandparents;


    public TCPGrandfatherControlPacket() {}


    public TCPGrandfatherControlPacket(GF_PROTOCOL protocol) {
        super(TYPE.CONTROL_GRANDFATHER);
        this.protocol = protocol;
        this.grandparents = new ArrayList<>();
    }


    public TCPGrandfatherControlPacket(GF_PROTOCOL protocol, List<String> grandparents) {
        super(TYPE.CONTROL_GRANDFATHER);
        this.protocol = protocol;
        this.grandparents = new ArrayList<>(grandparents);
    }


    public TCPGrandfatherControlPacket(TCPGrandfatherControlPacket packet) {
        super(packet.getType(), packet.getReceiver(), packet.getSender());
        this.protocol = packet.getProtocol();
        this.grandparents = packet.getGrandparents();
    }


    public TCPGrandfatherControlPacket clone(){
        return new TCPGrandfatherControlPacket(this);
    }


    public GF_PROTOCOL getProtocol(){
        return this.protocol;
    }


    public List<String> getGrandparents(){
        return this.grandparents.stream().collect(Collectors.toList());
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tProtocol: ").append(this.protocol.name());
        buffer.append("\tGrandfather: ").append(this.grandparents);
        return buffer.toString();
    }


    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPGrandfatherControlPacket.class);
        kryo.register(TCPGrandfatherControlPacket.GF_PROTOCOL.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPGrandfatherControlPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPGrandfatherControlPacket.class);
        kryo.register(TCPGrandfatherControlPacket.GF_PROTOCOL.class);

        TCPGrandfatherControlPacket packet = kryo.readObject(input,TCPGrandfatherControlPacket.class);
        input.close();

        return packet;
    }
}