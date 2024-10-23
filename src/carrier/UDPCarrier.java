package carrier;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import packet.udp.UDPStreamPacket;


public class UDPCarrier{


    public UDPCarrier(){}


    public void send(InputStream inputStream, InetSocketAddress socketAddress) throws SocketException, IOException{

        DatagramSocket socket = new DatagramSocket();
        UDPGroupCarrier groupCarrier = new UDPGroupCarrier(socket,socketAddress);
        List<UDPStreamPacket> group = groupCarrier.getGroup(inputStream);

        while (group.size() > 0){
            System.out.println("A enviar grupo :: " + groupCarrier.getGroupCounter() + " (" + group.size() + ")");
            groupCarrier.sendGroup(group);
            groupCarrier.nextGroup();
            group = groupCarrier.getGroup(inputStream);
        }

        groupCarrier.close();
    }


    public void receive(OutputStream outputStream, InetSocketAddress socketAddress) throws SocketException, IOException{

        DatagramSocket socket = new DatagramSocket(socketAddress.getPort());
        UDPGroupCarrier groupCarrier = new UDPGroupCarrier(socket,socketAddress);
        List<UDPStreamPacket> group = groupCarrier.receiveGroup();

        while (group.size() > 0){

            System.out.println("Recebido grupo :: " + groupCarrier.getGroupCounter() + " (" + group.size() + ")");

            for (UDPStreamPacket packet : group){
                outputStream.write(packet.getPayload());
            }

            groupCarrier.nextGroup();
            group = groupCarrier.receiveGroup();
        }

        groupCarrier.close();
    }
}