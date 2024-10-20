package streaming;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import packet.UDPStreamPacket;


public class GroupCarrier{

    private static final int PAYLOAD_SIZE = 4096;
    private static final int BUFFER_SIZE = 5000;
    private static final int GROUP_SIZE = 100;

    private static final int SOCKET_SEND_TIMEOUT = 20;
    private static final int SOCKET_RECEIVE_TIMEOUT = 2000;
    private static final int ASSUMES_LOST_CONNECTION = 10000;

    private DatagramSocket socket;
    private InetSocketAddress inetSocketAddress;


    GroupCarrier(DatagramSocket socket, InetSocketAddress inetSocketAddress){
        this.socket = socket;
        this.inetSocketAddress = inetSocketAddress;
    }


    public void close(){
        this.socket.close();
    }


    public List<UDPStreamPacket> getGroup(InputStream inputStream, int groupID) throws IOException{

        int bytes_read;
        byte[] payload = new byte[PAYLOAD_SIZE];
        List<UDPStreamPacket> group = new ArrayList<>();

        for (int index = 0; index < GROUP_SIZE && (bytes_read = inputStream.read(payload)) > 0; index++){
            group.add(new UDPStreamPacket(groupID,index,payload,bytes_read));
        }

        return group;
    }


    public void sendGroup(List<UDPStreamPacket> group, int groupID) throws IOException{

        List<Boolean> acks = new ArrayList<>(Collections.nCopies(group.size(),false));        
        DatagramPacket ackPacket = new DatagramPacket(new byte[BUFFER_SIZE],BUFFER_SIZE);
        DatagramPacket sendPacket = new DatagramPacket(new byte[BUFFER_SIZE],BUFFER_SIZE,this.inetSocketAddress);

        long lastAck = System.currentTimeMillis();
        this.socket.setSoTimeout(SOCKET_SEND_TIMEOUT);
        boolean condition = acks.stream().anyMatch(x -> x == false);

        while (condition){

            for (int index = 0; index < group.size() && condition; index++){

                if (!acks.get(index)){
                    sendPacket.setData(group.get(index).serialize());
                    socket.send(sendPacket);
                }

                try{
                    socket.receive(ackPacket);
                    UDPStreamPacket udpPacket = UDPStreamPacket.deserialize(ackPacket.getData());
                    lastAck = System.currentTimeMillis();

                    if (udpPacket.getGroup() == groupID){
                        acks.set(udpPacket.getSeqNum(),true);
                        condition = acks.stream().anyMatch(x -> x == false);
                    }
                }

                catch (SocketTimeoutException e){
                    if (System.currentTimeMillis() - lastAck > ASSUMES_LOST_CONNECTION){
                        throw new IOException("Assume lost connection");
                    }
                }
            }
        }
    }


    public List<UDPStreamPacket> receiveGroup(int groupID) throws SocketException{

        List<UDPStreamPacket> group = new ArrayList<>();
        DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE],BUFFER_SIZE);
        DatagramPacket ackPacket = new DatagramPacket(new byte[BUFFER_SIZE],BUFFER_SIZE);

        this.socket.setSoTimeout(SOCKET_RECEIVE_TIMEOUT);

        try{

            while (!this.socket.isClosed()){

                this.socket.receive(receivePacket);
                UDPStreamPacket updPacket = UDPStreamPacket.deserialize(receivePacket.getData());

                if (updPacket.getGroup() != groupID){
                    break;
                }

                if (!group.contains(updPacket)){
                    group.add(updPacket);
                }

                ackPacket.setAddress(receivePacket.getAddress());
                ackPacket.setPort(receivePacket.getPort());
                ackPacket.setData(new UDPStreamPacket(
                    updPacket.getGroup(),
                    updPacket.getSeqNum()).serialize());
                this.socket.send(ackPacket);
            }
        }

        catch (Exception e) {}

        Collections.sort(group);
        return group;
    }
}