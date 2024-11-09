package service.establishconnection;

import packet.udp.UDPVideoControlPacket;
import service.core.stream.EdgeStreamWorker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientWaitEstablishConnection implements Runnable{
    public static final int CLIENT_CONNECTION_PORT = 19999;
    //New port/thread for each stream request, to avoid conflicts between requests
    public static int newConnectionPort = CLIENT_CONNECTION_PORT + 1;
    //Assume won't surpass 1000 bytes, will probably need adapting
    public static final int MAX_PACKET_SIZE = 1000;

    public ClientWaitEstablishConnection() {
    }

    //Increase port number each time the current value has been used, to get a whole new port (connection) in the next connection to be established
    private void updateConnectionPortValue(){
        newConnectionPort += 1;
    }

    public void run(){
        try(DatagramSocket serverSocket = new DatagramSocket(CLIENT_CONNECTION_PORT)){
            byte[] packetBuffer = new byte[MAX_PACKET_SIZE];
            DatagramPacket receivedPacket = new DatagramPacket(packetBuffer, MAX_PACKET_SIZE);

            while(true)
            {
                serverSocket.receive(receivedPacket);
                UDPVideoControlPacket request = UDPVideoControlPacket.deserialize(receivedPacket.getData());
                //If not a request, packet gets ignored
                if (request.getProtocol() != UDPVideoControlPacket.VIDEO_PROTOCOL.REQUEST) {
                    System.out.println("Received unexpected packet: " + request);
                }
                else {
                    System.out.println("Received stream request from " + receivedPacket.getAddress() + " !");

                    Thread edgeWorker = new Thread(new EdgeStreamWorker(newConnectionPort, receivedPacket.getAddress()));
                    edgeWorker.start();
                }
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
