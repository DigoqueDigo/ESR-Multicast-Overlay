package packet.tcp;
import packet.Packet;


public abstract class TCPPacket extends Packet{

    public enum TCP_TYPE {
        BOOTSTRAPPER,
        CONNECTION_STATE,
        CONTROL_FLOOD,
        CONTROL_GRANDFATHER,
        CONTROL_VIDEO
    }

    private TCP_TYPE type;


    public TCPPacket(TCP_TYPE type){
        super();
        this.type = type;
    }


    public TCPPacket(TCP_TYPE type, String receiver, String sender){
        super(receiver,sender);
        this.type = type;
    }


    public TCP_TYPE getType(){
        return this.type;
    }


    public abstract TCPPacket clone();


    public abstract byte[] serialize();


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("Type: ").append(this.type.name());
        buffer.append(super.toString());
        return buffer.toString();
    }
}