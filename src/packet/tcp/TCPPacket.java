package packet.tcp;


public abstract class TCPPacket{

    public enum TYPE {
        BOOTSTRAPPER,
        CONNECTION_STATE,
        CONTROL_FLOOD,
        CONTROL_GRANDFATHER
    }

    private TYPE type;
    private String sender;
    private String receiver;


    public TCPPacket() {}


    public TCPPacket(TYPE type){
        this.type = type;
        this.sender = new String();
        this.receiver = new String();
    }


    public TCPPacket(TYPE type, String receiver, String sender){
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }


    public TYPE getType(){
        return this.type;
    }


    public String getSender(){
        return this.sender;
    }


    public String getReceiver(){
        return this.receiver;
    }


    public void setSender(String sender){
        this.sender = sender;
    }


    public void setReceiver(String receiver){
        this.receiver = receiver;
    }


    public abstract byte[] serialize();

    public abstract TCPPacket clone();

    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("Type: ").append(this.type.name());
        buffer.append("\tSender: ").append(this.sender);
        buffer.append("\tReceiver: ").append(this.receiver);
        return buffer.toString();
    }
}