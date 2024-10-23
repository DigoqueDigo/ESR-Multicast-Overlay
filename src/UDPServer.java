import java.io.FileInputStream;
import java.net.InetSocketAddress;
import carrier.UDPCarrier;


public class UDPServer{

    private static final int UDP_PORT = 12345;
    private static final String UDP_ADDRESS = "localhost";

    public static void main(String args[]) throws Exception{

        FileInputStream fileInputStream= new FileInputStream(args[0]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(UDP_ADDRESS,UDP_PORT);
        UDPCarrier carrier = new UDPCarrier();

        carrier.send(fileInputStream,inetSocketAddress);
        fileInputStream.close();
    }
}