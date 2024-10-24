package server;

import carrier.TCPCarrier;
import org.json.JSONArray;
import org.json.JSONObject;
import packet.tcp.TCPNodeInfo;

import java.io.IOException;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {

        //Get input args
        String server_name = args[0];
        String bootstapper_ip = args[1];
        int bootstapper_port = Integer.parseInt(args[2]);

        // Create socket
        Socket socket = new Socket(bootstapper_ip,bootstapper_port);

        //Create connection request packet
        TCPNodeInfo connection_request = new TCPNodeInfo(server_name);

        //Send packet through socket
        TCPCarrier tcpCarrier = new TCPCarrier(socket.getInputStream(),socket.getOutputStream());
        tcpCarrier.send(connection_request);

        //Receive response and parse it
        TCPNodeInfo response = (TCPNodeInfo) tcpCarrier.receive();
        JSONObject response_json = response.getJsonObject();
        JSONArray neighbours = response_json .getJSONArray("neighbours");
        System.out.println(neighbours);
        socket.close();
    }
}