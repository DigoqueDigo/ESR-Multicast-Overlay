import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;


public class UDPServer{

    private static final int UDP_PORT = 12345;
    private static final String UDP_ADDRESS = "localhost";

    public static void main(String args[]) throws Exception{

        FileInputStream inputStream = new FileInputStream(args[0]);
        InetSocketAddress address = new InetSocketAddress(UDP_ADDRESS,UDP_PORT);
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(new byte[4096],4096,address);

        int read; 
        byte[] buffer = new byte[2048];

        while ((read = inputStream.read(buffer)) > 0){
            packet.setData(buffer);
            socket.send(packet);
        }
    }
}