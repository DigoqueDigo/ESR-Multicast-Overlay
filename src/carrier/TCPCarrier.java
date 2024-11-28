package carrier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import packet.tcp.TCPBootstrapperPacket;
import packet.tcp.TCPConnectionStatePacket;
import packet.tcp.TCPFloodControlPacket;
import packet.tcp.TCPGrandfatherControlPacket;
import packet.tcp.TCPVideoControlPacket;
import utils.Crypto;
import packet.tcp.TCPPacket;


public class TCPCarrier{

    private static Map<Class<?>,Function<byte[],TCPPacket>> deserializeMap = new HashMap<>();

    static{
        // Adicionar um metodo de deserialize por extensao de TCPPacket
        deserializeMap.put(TCPBootstrapperPacket.class, x -> TCPBootstrapperPacket.deserialize(x));
        deserializeMap.put(TCPFloodControlPacket.class, x -> TCPFloodControlPacket.deserialize(x));
        deserializeMap.put(TCPVideoControlPacket.class, x -> TCPVideoControlPacket.deserialize(x));
        deserializeMap.put(TCPConnectionStatePacket.class, x -> TCPConnectionStatePacket.deserialize(x));
        deserializeMap.put(TCPGrandfatherControlPacket.class, x -> TCPGrandfatherControlPacket.deserialize(x));
    }

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;


    public TCPCarrier(InputStream inputStream, OutputStream outputStream){
        this.dataInputStream = new DataInputStream(inputStream);
        this.dataOutputStream = new DataOutputStream(outputStream);
    }


    public void send(TCPPacket tcpPacket) throws Exception{

        byte[] serialize = tcpPacket.serialize();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeUTF(tcpPacket.getClass().getCanonicalName());
        dataOutputStream.writeInt(serialize.length);
        dataOutputStream.write(serialize);
        dataOutputStream.flush();

        byte[] plainText = byteArrayOutputStream.toByteArray();
        byte[] crypto = Crypto.encrypt(plainText);

        dataOutputStream.close();
        byteArrayOutputStream.close();

        this.dataOutputStream.writeInt(crypto.length);
        this.dataOutputStream.write(crypto);
        this.dataOutputStream.flush();
    }


    public TCPPacket receive() throws Exception{

        int criyto_size = this.dataInputStream.readInt();
        byte[] crypto = new byte[criyto_size];

        this.dataInputStream.readFully(crypto);
        byte[] plainText = Crypto.decrypt(crypto);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(plainText);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        Class<?> clazz = Class.forName(dataInputStream.readUTF());
        int serialize_size = dataInputStream.readInt();
        byte[] serialize = new byte[serialize_size];

        dataInputStream.readFully(serialize);
        dataInputStream.close();
        byteArrayInputStream.close();

        return deserializeMap.get(clazz).apply(serialize);
    }
}