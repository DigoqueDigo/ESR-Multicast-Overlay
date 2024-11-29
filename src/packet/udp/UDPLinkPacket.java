package packet.udp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class UDPLinkPacket extends UDPPacket{

    private long timestamp;


    public UDPLinkPacket(){
        super(UDP_TYPE.LINK);
        this.timestamp = System.currentTimeMillis();
    }


    public UDPLinkPacket(UDPLinkPacket packet){
        super(packet.getType(),
            packet.getReceiverIP(),
            packet.getReceiverPort(),
            packet.getSenderIP(),
            packet.getSenderPort());
        this.timestamp = packet.getTimestamp();
    }


    @Override
    public UDPLinkPacket clone(){
        return new UDPLinkPacket(this);
    }


    public long getTimestamp(){
        return this.timestamp;
    }


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("Timestamp: " + this.timestamp);
        return buffer.toString();
    }


    @Override
    public byte[] serialize(){

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(UDPPacket.UDP_TYPE.class);
        kryo.register(UDPLinkPacket.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static UDPLinkPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(UDPPacket.UDP_TYPE.class);
        kryo.register(UDPLinkPacket.class);

        UDPLinkPacket packet = kryo.readObject(input,UDPLinkPacket.class);
        input.close();

        return packet;
    }
}