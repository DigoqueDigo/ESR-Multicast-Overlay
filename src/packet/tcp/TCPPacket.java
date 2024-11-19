package packet.tcp;
import packet.Packet;


public abstract class TCPPacket extends Packet{

    public enum TCP_TYPE {
        BOOTSTRAPPER,
        CONTROL_CONNECTION_STATE,
        CONTROL_FLOOD,
        CONTROL_GRANDFATHER,
        CONTROL_VIDEO
    }

    private TCP_TYPE type;


    public TCPPacket(TCP_TYPE type){
        super();
        this.type = type;
    }


    public TCPPacket(TCP_TYPE type, String receiverIP, int receiverPort, String senderIP, int senderPort){
        super(receiverIP,receiverPort,senderIP,senderPort);
        this.type = type;
    }


    public TCP_TYPE getType(){
        return this.type;
    }


    public abstract TCPPacket clone();


    public abstract byte[] serialize();


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("Type: " + this.type.name());
        buffer.append(super.toString());
        return buffer.toString();
    }
}