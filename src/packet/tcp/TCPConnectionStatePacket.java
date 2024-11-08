package packet.tcp;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class TCPConnectionStatePacket extends TCPPacket{

    public enum CS_PROTOCOL{
        CONNECTION_LOST
    }

    private CS_PROTOCOL protocol;


    public TCPConnectionStatePacket(){
        super(TCP_TYPE.CONNECTION_STATE);
    }


    public TCPConnectionStatePacket(CS_PROTOCOL protocol, String receiver, String sender) {
        super(TCP_TYPE.CONNECTION_STATE, receiver, sender);
        this.protocol = protocol;
    }


    public TCPConnectionStatePacket(TCPConnectionStatePacket tcpStatePacket){
        super(TCP_TYPE.CONNECTION_STATE, tcpStatePacket.getReceiver(), tcpStatePacket.getSender());
        this.protocol = tcpStatePacket.getProtocol();
    }


    public TCPConnectionStatePacket clone(){
        return new TCPConnectionStatePacket(this);
    }


    public CS_PROTOCOL getProtocol() {
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

        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(TCPConnectionStatePacket.class);
        kryo.register(TCPConnectionStatePacket.CS_PROTOCOL.class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static TCPConnectionStatePacket deserialize(byte[] data) {

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPPacket.TCP_TYPE.class);
        kryo.register(TCPConnectionStatePacket.class);
        kryo.register(TCPConnectionStatePacket.CS_PROTOCOL.class);

        TCPConnectionStatePacket packet = kryo.readObject(input,TCPConnectionStatePacket.class);
        input.close();

        return packet;
    }
}