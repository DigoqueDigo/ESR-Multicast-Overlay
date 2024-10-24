package packet.tcp;


public abstract class TCPPacket{

    public enum TYPE { NODEINFO }

    private TYPE type;


    public TCPPacket() {}


    public TCPPacket(TYPE type){
        this.type = type;
    }


    public TYPE getType(){
        return this.type;
    }


    public abstract byte[] serialize();
}