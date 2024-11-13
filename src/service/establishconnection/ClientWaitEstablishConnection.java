package service.establishconnection;
import packet.udp.UDPPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import carrier.UDPCarrier;


public class ClientWaitEstablishConnection implements Runnable{

    public static final String CLIENT_ESTABLISH_CONNECTION_ADDRESS = "0.0.0.0";
    public static final int CLIENT_ESTABLISH_CONNECTION_PORT = 5000;

    private UDPCarrier udpCarrier;


    public ClientWaitEstablishConnection() throws SocketException{
        InetSocketAddress socketAddress = new InetSocketAddress(
            CLIENT_ESTABLISH_CONNECTION_ADDRESS,CLIENT_ESTABLISH_CONNECTION_PORT);
        this.udpCarrier = new UDPCarrier(socketAddress);
    }


    public void run(){

        try{

            UDPPacket udpPacket;
            System.out.println("ClientWaitEstablishConnection service started");

            while (this.udpCarrier.isClosed() == false){

                if ((udpPacket = udpCarrier.receive()) != null){
                    System.out.println("ClientWaitEstablishConnection received packet: " + udpPacket);
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }    
    }
}