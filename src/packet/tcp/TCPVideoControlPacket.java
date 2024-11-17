package packet.tcp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class TCPVideoControlPacket extends TCPPacket{

    public enum CORE_VIDEO_PROTOCOL{
        REQUEST,
        CANCEL,
        REPLY
    }

    private CORE_VIDEO_PROTOCOL protocol;
    private String video;
    private byte[] data;


    public TCPVideoControlPacket(){
        super(TCP_TYPE.CONTROL_VIDEO);
        this.protocol = null;
        this.video = null;
        this.data = null;
    }


    public TCPVideoControlPacket(CORE_VIDEO_PROTOCOL protocol, String video, byte[] data, String receiver, String sender){
        super(TCP_TYPE.CONTROL_VIDEO, receiver, sender);
        this.protocol = protocol;
        this.video = video;
        this.data = data;
    }


    public TCPVideoControlPacket(TCPVideoControlPacket packet){
        super(packet.getType(), packet.getReceiver(), packet.getSender());
        this.protocol = packet.getProtocol();
        this.video = packet.getVideo();
        this.data = packet.getData();
    }


    public TCPVideoControlPacket clone(){
        return new TCPVideoControlPacket(this);
    }


    public CORE_VIDEO_PROTOCOL getProtocol(){
        return this.protocol;
    }


    public String getVideo(){
        return this.video;
    }


    public byte[] getData(){
        return Arrays.copyOf(this.data,this.data.length);
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tProtocol: ").append(this.protocol.name());
        buffer.append("\tVideo: ").append(this.video);
        buffer.append("\tData length: ").append(this.data.length);
        return buffer.toString();
    }


    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(byte[].class);
        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(CORE_VIDEO_PROTOCOL.class);
        kryo.register(TCPVideoControlPacket.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPVideoControlPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(byte[].class);
        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(CORE_VIDEO_PROTOCOL.class);
        kryo.register(TCPVideoControlPacket.class);

        TCPVideoControlPacket packet = kryo.readObject(input,TCPVideoControlPacket.class);
        input.close();

        return packet;
    }
}