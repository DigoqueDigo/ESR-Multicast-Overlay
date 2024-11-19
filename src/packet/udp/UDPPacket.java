package packet.udp;
import packet.Packet;


public abstract class UDPPacket extends Packet{

    public enum UDP_TYPE{
        ACK,
        VIDEO_LIST,
        VIDEO_CONTROL
    }

    private long id;
    private UDP_TYPE type;


    public UDPPacket(UDP_TYPE type){
        super();
        this.type = type;
        this.id = System.nanoTime();
    }


    public UDPPacket(UDP_TYPE type, String receiverIP, int receiverPort, String senderIP, int senderPort){
        super(receiverIP,receiverPort,senderIP,senderPort);
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
        buffer.append("Type: " + this.type.name());
        buffer.append("ID: " + this.id);
        buffer.append(super.toString());
        return buffer.toString();
    }   
}