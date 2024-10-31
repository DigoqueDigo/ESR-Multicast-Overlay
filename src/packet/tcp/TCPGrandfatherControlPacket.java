package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class TCPGrandfatherControlPacket extends TCPPacket {

    private String grandfather;


    public TCPGrandfatherControlPacket() {
        super(TYPE.CONTROL_GRANDFATHER);
    }

    public TCPGrandfatherControlPacket(String grandfather) {
        super(TYPE.CONTROL_GRANDFATHER);
        this.grandfather = grandfather;
    }

    public TCPGrandfatherControlPacket(TCPGrandfatherControlPacket packet) {
        super(packet.getType(), packet.getSender(), packet.getReceiver());
        this.grandfather = packet.getGrandfather();
    }


    public String getGrandfather() {
        return grandfather;
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tGrandfather: ").append(this.grandfather);
        return buffer.toString();
    }


    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPGrandfatherControlPacket.class);
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

        TCPGrandfatherControlPacket packet = kryo.readObject(input,TCPGrandfatherControlPacket.class);
        input.close();

        return packet;
    }

    public TCPGrandfatherControlPacket clone(){
        return new TCPGrandfatherControlPacket(this);
    }

}