import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import carrier.UDPCarrier;


public class UDPClient{

    private static final int UDP_PORT = 12345;
    private static final String UDP_ADDRESS = "localhost";

    public static void main(String args[]) throws Exception{

        FileOutputStream fileOutputStream = new FileOutputStream(args[0]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(UDP_ADDRESS,UDP_PORT);
        UDPCarrier carrier = new UDPCarrier();

        carrier.receive(fileOutputStream,inetSocketAddress);
        fileOutputStream.close();
    }
}