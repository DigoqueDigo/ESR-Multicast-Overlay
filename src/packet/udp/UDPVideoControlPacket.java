package packet.udp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class UDPVideoControlPacket extends UDPPacket{

    public enum EDGE_VIDEO_PROTOCOL{
        VIDEO_REQUEST,
        VIDEO_CANCEL
    }

    private String video; 
    private EDGE_VIDEO_PROTOCOL protocol;


    public UDPVideoControlPacket(){
        super(UDP_TYPE.VIDEO_CONTROL);
    }


    public UDPVideoControlPacket(EDGE_VIDEO_PROTOCOL protocol, String video){
        super(UDP_TYPE.VIDEO_CONTROL);
        this.protocol = protocol;
        this.video = video;
    }


    public UDPVideoControlPacket(UDPVideoControlPacket packet){
        super(packet.getType(),
            packet.getReceiverIP(),
            packet.getReceiverPort(),
            packet.getSenderIP(),
            packet.getSenderPort());
        this.protocol = packet.getProtocol();
        this.video = packet.getVideo();
    }


    @Override
    public UDPVideoControlPacket clone(){
        return new UDPVideoControlPacket(this);
    }


    public EDGE_VIDEO_PROTOCOL getProtocol(){
        return this.protocol;
    }


    public String getVideo(){
        return this.video;
    }


    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tProtocol: " + this.protocol.name());
        buffer.append("\tVideo: " + this.video);
        return buffer.toString();
    }


    @Override
    public byte[] serialize(){

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(UDPPacket.UDP_TYPE.class);
        kryo.register(UDPVideoControlPacket.class);
        kryo.register(UDPVideoControlPacket.EDGE_VIDEO_PROTOCOL.class);
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
        kryo.register(UDPVideoControlPacket.EDGE_VIDEO_PROTOCOL.class);

        UDPVideoControlPacket packet = kryo.readObject(input,UDPVideoControlPacket.class);
        input.close();

        return packet;
    }
}