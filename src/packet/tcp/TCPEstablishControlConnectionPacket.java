package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class TCPEstablishControlConnectionPacket extends TCPPacket {

    private String myIPinterface;
    private String neighbourIPinterface;

    public TCPEstablishControlConnectionPacket() {
        super(TYPE.CONTROL_ESTABLISH_CONNECTION);
    }

    public TCPEstablishControlConnectionPacket(String myIPinterface, String neighbourIPInterface) {
        super(TYPE.CONTROL_ESTABLISH_CONNECTION);
        this.myIPinterface = myIPinterface;
        this.neighbourIPinterface = neighbourIPInterface;
    }

    public String getMyIPinterface() {
        return this.myIPinterface;
    }

    public String getNeighbourIPinterface() {
        return this.neighbourIPinterface;
    }

    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPEstablishControlConnectionPacket.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static TCPEstablishControlConnectionPacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPEstablishControlConnectionPacket.class);
        TCPEstablishControlConnectionPacket packet = kryo.readObject(input,TCPEstablishControlConnectionPacket.class);
        input.close();

        return packet;
    }
}
