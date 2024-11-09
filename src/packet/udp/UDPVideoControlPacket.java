package packet.udp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class UDPVideoControlPacket extends UDPPacket{

    public enum VIDEO_PROTOCOL{
        REQUEST,
        REPLY,
        CANCEL
    }

    private VIDEO_PROTOCOL protocol;
    //To be added later
    //private String stream_id;


    public UDPVideoControlPacket(){
        super(UDP_TYPE.CONTROL_VIDEO);
    }


    public UDPVideoControlPacket(VIDEO_PROTOCOL protocol, String receiver, String sender){
        super(UDP_TYPE.CONTROL_VIDEO, receiver, sender);
        this.protocol = protocol;
    }


    public UDPVideoControlPacket(UDPVideoControlPacket packet){
        super(UDP_TYPE.CONTROL_VIDEO, packet.getReceiver(), packet.getSender());
        this.protocol = packet.getProtocol();
    }


    public UDPVideoControlPacket clone(){
        return new UDPVideoControlPacket(this);
    }


    public VIDEO_PROTOCOL getProtocol(){
        return this.protocol;
    }


    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tProtocol: ").append(this.protocol.name());
        return buffer.toString();
    }


    @Override
    public byte[] serialize(){

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(UDPPacket.UDP_TYPE.class);
        kryo.register(UDPVideoControlPacket.class);
        kryo.register(UDPVideoControlPacket.VIDEO_PROTOCOL.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static UDPVideoControlPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(UDPPacket.UDP_TYPE.class);
        kryo.register(UDPVideoControlPacket.class);
        kryo.register(UDPVideoControlPacket.VIDEO_PROTOCOL.class);

        UDPVideoControlPacket packet = kryo.readObject(input,UDPVideoControlPacket.class);
        input.close();

        return packet;
    }
}