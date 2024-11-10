package packet.udp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class UDPAckPacket extends UDPPacket{


    public UDPAckPacket(){
        super(UDP_TYPE.ACK);
    }


    public UDPAckPacket(String receiver, String sender){
        super(UDP_TYPE.ACK, receiver, sender);
    }


    public UDPAckPacket(UDPAckPacket packet){
        super(UDP_TYPE.ACK, packet.getReceiver(), packet.getSender());
    }


    @Override
    public UDPAckPacket clone(){
        return new UDPAckPacket(this);
    }


    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        return buffer.toString();
    }


    @Override
    public byte[] serialize(){

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(UDPPacket.UDP_TYPE.class);
        kryo.register(UDPAckPacket.class);
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
        kryo.register(UDPAckPacket.class);

        UDPVideoControlPacket packet = kryo.readObject(input,UDPVideoControlPacket.class);
        input.close();

        return packet;
    }   
}