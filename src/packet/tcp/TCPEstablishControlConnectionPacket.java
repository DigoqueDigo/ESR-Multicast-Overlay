package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class TCPEstablishControlConnectionPacket extends TCPPacket {

    private String sender;

    public TCPEstablishControlConnectionPacket() {
        super(TYPE.CONTROL_ESTABLISH_CONNECTION);
    }

    public TCPEstablishControlConnectionPacket(String sender) {
        super(TYPE.CONTROL_ESTABLISH_CONNECTION);
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public byte[] serialize() {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPEstablishControlConnectionPacket.class);
        kryo.writeObject(output,this.sender);
        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static TCPEstablishControlConnectionPacket deserialize(byte[] data) {
        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPEstablishControlConnectionPacket.class);
        String sender = kryo.readObject(input,String.class);
        TCPEstablishControlConnectionPacket packet = new TCPEstablishControlConnectionPacket(sender);
        input.close();

        return packet;
    }
}
