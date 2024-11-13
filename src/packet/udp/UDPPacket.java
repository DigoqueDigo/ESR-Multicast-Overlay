package packet.udp;
import packet.Packet;


public abstract class UDPPacket extends Packet{

    public enum UDP_TYPE{
        CONTROL_VIDEO,
        ACK
    }

    private long id;
    private UDP_TYPE type;


    public UDPPacket(UDP_TYPE type){
        super();
        this.type = type;
        this.id = System.nanoTime();
    }


    public UDPPacket(UDP_TYPE type, String receiver, String sender){
        super(receiver, sender);
        this.type = type;
        this.id = System.nanoTime();
    }


    public UDP_TYPE getType(){
        return this.type;
    }


    public long getID(){
        return this.id;
    }


    public void setID(long id){
        this.id = id;
    }


    public abstract UDPPacket clone();


    public abstract byte[] serialize();


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("Type: ").append(this.type.name());
        buffer.append("ID: ").append(this.id);
        buffer.append(super.toString());
        return buffer.toString();
    }   
}