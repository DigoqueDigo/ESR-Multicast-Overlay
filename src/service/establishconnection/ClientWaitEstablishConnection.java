package service.establishconnection;
import packet.udp.UDPPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import carrier.UDPCarrier;


public class ClientWaitEstablishConnection implements Runnable{

    public static final String CLIENT_CONNECTION_ADDRESS = "0.0.0.0";
    public static final int CLIENT_CONNECTION_PORT = 4000;

    private UDPCarrier udpCarrier;


    public ClientWaitEstablishConnection() throws SocketException{
        this.udpCarrier = new UDPCarrier();
    }


    public void run(){

        try{

            UDPPacket udpPacket;
            InetSocketAddress socketAddress = new InetSocketAddress(CLIENT_CONNECTION_ADDRESS,CLIENT_CONNECTION_PORT);

            this.udpCarrier.bind(socketAddress);

            while (this.udpCarrier.isClosed() == false){

                if ((udpPacket = udpCarrier.receive()) != null){
                    System.out.println(udpPacket);
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }    
    }
}