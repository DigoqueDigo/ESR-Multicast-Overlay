import java.io.FileInputStream;
import java.net.InetSocketAddress;
import streaming.Carrier;


public class UDPServer{

    private static final int UDP_PORT = 12345;
    private static final String UDP_ADDRESS = "localhost";

    public static void main(String args[]) throws Exception{

        FileInputStream fileInputStream= new FileInputStream(args[0]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(UDP_ADDRESS,UDP_PORT);
        Carrier carrier = new Carrier();

        carrier.send(fileInputStream,inetSocketAddress);
        fileInputStream.close();
    }
}