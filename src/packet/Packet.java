package packet;


public abstract class Packet{

    private String sender;
    private String receiver;


    public Packet(){
        this.sender = new String();
        this.receiver = new String();
    }


    public Packet(String receiver, String sender){
        this.sender = sender;
        this.receiver = receiver;
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


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("\tSender: ").append(this.sender);
        buffer.append("\tReceiver: ").append(this.receiver);
        return buffer.toString();
    }
}