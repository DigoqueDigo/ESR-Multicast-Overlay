package service.core.stream;

import packet.udp.UDPVideoControlPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//Worker that only exists in edge nodes, handles communication between Client and Server
public class EdgeStreamWorker implements Runnable{
    public static final int CLIENT_CONNECTION_PORT = 19999;
    private final int port;
    //I don't know another way to put the initially received packet inside this thread unless it's part of the class
    private InetAddress clientAddress;

    public EdgeStreamWorker(int port, InetAddress clientAddress) {
        this.port = port;
        this.clientAddress = clientAddress;
    }

    @Override
    public void run(){

        //Create new UDP socket that handles transmission to client
        try (DatagramSocket socket = new DatagramSocket(port)){
            //Send acknowledgement packet back to sender, for them to know the packet was not lost and the connection became finalized
            UDPVideoControlPacket response = new UDPVideoControlPacket(UDPVideoControlPacket.VIDEO_PROTOCOL.REPLY, clientAddress.getHostName(), "");
            DatagramPacket sendPacket = new DatagramPacket(response.serialize(), response.serialize().length, clientAddress, CLIENT_CONNECTION_PORT);
            socket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
