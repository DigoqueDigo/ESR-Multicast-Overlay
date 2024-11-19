package packet.udp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class UDPVideoListPacket extends UDPPacket{

    private List<String> videos; 


    public UDPVideoListPacket(){
        super(UDP_TYPE.VIDEO_LIST);
    }


    public UDPVideoListPacket(List<String> videos){
        super(UDP_TYPE.VIDEO_LIST);
        this.videos = videos.stream().collect(Collectors.toList());
    }


    public UDPVideoListPacket(UDPVideoListPacket packet){
        super(packet.getType(),
            packet.getReceiverIP(),
            packet.getReceiverPort(),
            packet.getSenderIP(),
            packet.getSenderPort());
        this.videos = packet.getVideos();
    }


    @Override
    public UDPVideoListPacket clone(){
        return new UDPVideoListPacket(this);
    }


    public List<String> getVideos(){
        return this.videos.stream().collect(Collectors.toList());
    }


    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tVideos: " + this.videos);
        return buffer.toString();
    }


    @Override
    public byte[] serialize(){

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(ArrayList.class);
        kryo.register(UDPPacket.UDP_TYPE.class);
        kryo.register(UDPVideoListPacket.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static UDPVideoListPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(ArrayList.class);
        kryo.register(UDPPacket.UDP_TYPE.class);
        kryo.register(UDPVideoListPacket.class);

        UDPVideoListPacket packet = kryo.readObject(input,UDPVideoListPacket.class);
        input.close();

        return packet;
    }
}