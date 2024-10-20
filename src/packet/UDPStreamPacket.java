package packet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class UDPStreamPacket implements Comparable<UDPStreamPacket>{

    private int group;
    private int seq_num;
    private byte payload[];


    public UDPStreamPacket() {}


    public UDPStreamPacket(int group, int seq_num){
        this.group = group;
        this.seq_num = seq_num;
        this.payload = new byte[0];
    }


    public UDPStreamPacket(int group, int seq_num, byte payload[], int length){
        this.group = group;
        this.seq_num = seq_num;
        this.payload = Arrays.copyOf(payload,length);
    }


    public int getGroup(){
        return this.group;
    }

    
    public int getSeqNum(){
        return this.seq_num;
    }


    public byte[] getPayload(){
        return this.payload;
    }


    public int compareTo(UDPStreamPacket udpPacket){
        return Integer.compare(this.seq_num,udpPacket.seq_num);
    }

    public boolean equals(Object obj){

        if (this == obj) return true;

        if (obj == null || this.getClass() != obj.getClass()) return false;

        UDPStreamPacket that = (UDPStreamPacket) obj;
        return this.seq_num == that.seq_num;
    }


    public byte[] serialize(){

        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.register(UDPStreamPacket.class);
        kryo.register(byte[].class);
        kryo.writeObject(output,this);

        output.flush();
        output.close();

        return byteArrayOutputStream.toByteArray();
    }


    public static UDPStreamPacket deserialize(byte[] data){

        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);

        kryo.register(UDPStreamPacket.class);
        kryo.register(byte[].class);

        UDPStreamPacket udp_packet = kryo.readObject(input,UDPStreamPacket.class);
        input.close();

        return udp_packet;
    }
}