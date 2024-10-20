import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;


public class UDPClient{

    private static final int UDP_PORT = 12345;
    private static final String UDP_ADDRESS = "localhost";

    public static void main(String args[]) throws Exception{

        FileOutputStream fileOutputStream = new FileOutputStream("fifo");
        
        DatagramSocket socket = new DatagramSocket(12345);
        DatagramPacket packet = new DatagramPacket(new byte[4096],4096);

      //  socket.setSoTimeout(20000);

        while (true){

            socket.receive(packet);
            System.out.println("AAAAAAAAAAA");
            fileOutputStream.write(packet.getData());
        }




    }
}