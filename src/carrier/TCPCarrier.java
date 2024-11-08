package carrier;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import packet.tcp.TCPBootstrapperPacket;
import packet.tcp.TCPConnectionStatePacket;
import packet.tcp.TCPFloodControlPacket;
import packet.tcp.TCPGrandfatherControlPacket;
import packet.tcp.TCPPacket;


public class TCPCarrier{

    private static Map<Class<?>,Function<byte[],TCPPacket>> deserializeMap = new HashMap<>();

    static{
        // Adicionar um metodo de deserialize por extensao de TCPPacket
        deserializeMap.put(TCPBootstrapperPacket.class, x -> TCPBootstrapperPacket.deserialize(x));
        deserializeMap.put(TCPFloodControlPacket.class, x -> TCPFloodControlPacket.deserialize(x));
        deserializeMap.put(TCPGrandfatherControlPacket.class, x -> TCPGrandfatherControlPacket.deserialize(x));
        deserializeMap.put(TCPConnectionStatePacket.class, x -> TCPConnectionStatePacket.deserialize(x));
    }

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;


    public TCPCarrier(InputStream inputStream, OutputStream outputStream){
        this.dataInputStream = new DataInputStream(inputStream);
        this.dataOutputStream = new DataOutputStream(outputStream);
    }


    public void send(TCPPacket tcpPacket) throws IOException{
        byte[] data = tcpPacket.serialize();
        this.dataOutputStream.writeUTF(tcpPacket.getClass().getCanonicalName());
        this.dataOutputStream.writeInt(data.length);
        this.dataOutputStream.write(data);
        this.dataOutputStream.flush();
    }


    public TCPPacket receive() throws IOException, ClassNotFoundException{

        Class<?> clazz = Class.forName(this.dataInputStream.readUTF());
        int data_size = this.dataInputStream.readInt();
        byte data[] = new byte[data_size];

        if (this.readAllBytes(dataInputStream,data,data_size) != data_size){
            throw new IOException("TCP packet reading incomplete");
        }

        return deserializeMap.get(clazz).apply(data);
    }


    public int readAllBytes(InputStream inputStream, byte[] data, int length) throws IOException{

        int  attempt = 1;
        int bytes_read = 0;

        for (int rest = length; attempt > 0 && bytes_read < length; rest -= attempt){

            attempt = inputStream.read(data,bytes_read,rest);
            if (attempt > 0) bytes_read += attempt;
        }

        return bytes_read;
    }
}