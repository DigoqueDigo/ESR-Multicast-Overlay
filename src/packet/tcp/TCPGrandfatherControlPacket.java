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

    public String getGrandfather() {
        return grandfather;
    }

    @Override
    public byte[] serialize() {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(TCPGrandfatherControlPacket.class);
        kryo.writeObject(output, this.grandfather);
        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static TCPGrandfatherControlPacket deserialize(byte[] data) {
        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(TCPGrandfatherControlPacket.class);
        String sender = kryo.readObject(input,String.class);
        TCPGrandfatherControlPacket packet = new TCPGrandfatherControlPacket(sender);
        input.close();

        return packet;
    }
}
