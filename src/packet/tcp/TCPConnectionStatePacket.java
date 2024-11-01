package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class TCPConnectionStatePacket extends TCPPacket {

    public enum PROTOCOL{
        CONNECTION_LOST
    }

    private PROTOCOL protocol;

    public TCPConnectionStatePacket() {}


    public TCPConnectionStatePacket(PROTOCOL protocol, String receiver, String sender) {
        super(TYPE.CONNECTION_STATE, receiver, sender);
        this.protocol = protocol;
    }


    public TCPConnectionStatePacket(TCPConnectionStatePacket tcpStatePacket){
        super(TYPE.CONNECTION_STATE, tcpStatePacket.getReceiver(), tcpStatePacket.getSender());
        this.protocol = tcpStatePacket.getProtocol();
    }


    public TCPConnectionStatePacket clone(){
        return new TCPConnectionStatePacket(this);
    }


    public PROTOCOL getProtocol() {
        return this.protocol;
    }


    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("\tPROTOCOL: ").append(this.protocol.name());
        return buffer.toString();
    }


    @Override
    public byte[] serialize() {

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPConnectionStatePacket.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPConnectionStatePacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPPacket.TYPE.class);
        kryo.register(TCPConnectionStatePacket.class);

        TCPConnectionStatePacket packet = kryo.readObject(input,TCPConnectionStatePacket.class);
        input.close();

        return packet;
    }
}