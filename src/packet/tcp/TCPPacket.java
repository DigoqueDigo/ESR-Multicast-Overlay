package packet.tcp;


public abstract class TCPPacket{

    public enum TYPE {
        BOOTSTRAPPER,
        CONTROL_ESTABLISH_CONNECTION,
        CONTROL_FLOOD,
        CONTROL_GRANDFATHER}

    private TYPE type;


    public TCPPacket() {}


    public TCPPacket(TYPE type){
        this.type = type;
    }


    public TYPE getType(){
        return this.type;
    }


    public abstract byte[] serialize();


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("TYPE: ").append(this.type.name());
        return buffer.toString();
    }
}