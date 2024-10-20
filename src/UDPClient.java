import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import streaming.Carrier;


public class UDPClient{

    private static final int UDP_PORT = 12345;
    private static final String UDP_ADDRESS = "localhost";

    public static void main(String args[]) throws Exception{

        FileOutputStream fileOutputStream = new FileOutputStream(args[0]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(UDP_ADDRESS,UDP_PORT);
        Carrier carrier = new Carrier();

        carrier.receive(fileOutputStream,inetSocketAddress);
        fileOutputStream.close();
    }
}