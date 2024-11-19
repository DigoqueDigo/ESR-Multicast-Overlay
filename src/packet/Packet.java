package packet;


public abstract class Packet{

    private String senderIP;
    private String receiverIP;
    private int senderPort;
    private int receiverPort;


    public Packet(){
        this.senderPort = -1;
        this.receiverPort = -1;
    }


    public Packet(String receiverIP, int receiverPort, String senderIP, int senderPort){
        this.senderIP = senderIP;
        this.senderPort = senderPort;
        this.receiverIP = receiverIP;
        this.receiverPort = receiverPort;
    }


    public String getSenderIP(){
        return this.senderIP;
    }


    public int getSenderPort(){
        return this.senderPort;
    }


    public String getReceiverIP(){
        return this.receiverIP;
    }


    public int getReceiverPort(){
        return this.receiverPort;
    }


    public void setSenderIP(String senderIP){
        this.senderIP = senderIP;
    }


    public void setSenderPort(int senderPort){
        this.senderPort = senderPort;
    }


    public void setReceiverIP(String receiverIP){
        this.receiverIP = receiverIP;
    }


    public void setReceiverPort(int receiverPort){
        this.receiverPort = receiverPort;
    }


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("\tSender: " + this.senderIP + ":" + this.senderPort);
        buffer.append("\tReceiver: " + this.receiverIP + ":" + this.receiverPort);
        return buffer.toString();
    }
}