package streaming;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import packet.UDPStreamPacket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Carrier{


    public Carrier(){}


    public void send(InputStream inputStream, InetSocketAddress socketAddress) throws SocketException, IOException{

        int groupID = 0;
        DatagramSocket socket = new DatagramSocket();
        GroupCarrier groupCarrier = new GroupCarrier(socket,socketAddress);
        List<UDPStreamPacket> group = groupCarrier.getGroup(inputStream,groupID);

        while (group.size() > 0){
            System.out.println("A enviar grupo :: " + groupID + " (" + group.size() + ")");
            groupCarrier.sendGroup(group,groupID++);
            group = groupCarrier.getGroup(inputStream,groupID);
        }

        groupCarrier.close();
    }


    public void receive(OutputStream outputStream, InetSocketAddress socketAddress) throws SocketException, IOException{

        int groupID = 0;
        DatagramSocket socket = new DatagramSocket(socketAddress.getPort());
        GroupCarrier groupCarrier = new GroupCarrier(socket,socketAddress);
        List<UDPStreamPacket> group = groupCarrier.receiveGroup(groupID);

        while (group.size() > 0){

            System.out.println("Recebido grupo :: " + groupID + " (" + group.size() + ")");

            for (UDPStreamPacket packet : group){
                outputStream.write(packet.getPayload());
            }

            group = groupCarrier.receiveGroup(++groupID);
        }
        groupCarrier.close();     
    }
}