package carrier;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import packet.udp.UDPAckPacket;
import packet.udp.UDPPacket;
import packet.udp.UDPVideoControlPacket;
import packet.udp.UDPPacket.UDP_TYPE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class UDPCarrier{

    private static final int BUFFER_SIZE = 4096;
    private static final int SEND_TIMEOUT = 50;
    private static final int RECEIVE_TIMEOUT = 500;
    private static Map<Class<?>,Function<byte[],UDPPacket>> deserializeMap = new HashMap<>();

    static{
        // Adicionar um metodo de deserialize por extensao de TCPPacket
        deserializeMap.put(UDPVideoControlPacket.class, x -> UDPVideoControlPacket.deserialize(x));
        deserializeMap.put(UDPAckPacket.class, x -> UDPAckPacket.deserialize(x));
    }

    private DatagramSocket socket;


    public UDPCarrier() throws SocketException{
        this.socket = new DatagramSocket();
    }


    public UDPCarrier(SocketAddress socketAddress) throws SocketException{
        this.socket = new DatagramSocket(socketAddress);
    }


    public void connect(SocketAddress socketAddress) throws SocketException{
        this.socket.connect(socketAddress);
    }


    public void disconnect(){
        this.socket.disconnect();
    }


    public boolean isClosed(){
        return this.socket.isClosed();
    }


    public void close(){
        this.socket.close();
    }


    private byte[] prepare_serialize(UDPPacket udpPacket) throws IOException{

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        byte[] serialize = udpPacket.serialize();
        dataOutputStream.writeUTF(udpPacket.getClass().getCanonicalName());
        dataOutputStream.writeInt(serialize.length);
        dataOutputStream.write(serialize);

        byte[] data = byteArrayOutputStream.toByteArray();
        dataOutputStream.close();
        byteArrayOutputStream.close();

        return data;
    }


    private UDPPacket prepare_deserialize(byte[] data) throws IOException, ClassNotFoundException{

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        Class<?> clazz = Class.forName(dataInputStream.readUTF());
        int deserialize_size = dataInputStream.readInt();
        byte[] deserialize = new byte[deserialize_size];

        if (IOUtils.readAllBytes(dataInputStream,deserialize,deserialize_size) != deserialize_size){
            throw new IOException("UDPPacket reading incomplete");
        }

        UDPPacket udpPacket = deserializeMap.get(clazz).apply(deserialize);

        dataInputStream.close();
        byteArrayInputStream.close();

        return udpPacket;
    }


    public void send(UDPPacket udpPacket) throws IOException, ClassNotFoundException{

        boolean condition = true;
        byte[] data = this.prepare_serialize(udpPacket);

        DatagramPacket sendPacket = new DatagramPacket(data, data.length);
        DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        this.socket.setSoTimeout(SEND_TIMEOUT);

        while (condition){

            this.socket.send(sendPacket);

            try{

                this.socket.receive(receivePacket);
                byte[] receive_data = receivePacket.getData();
                UDPPacket receiveUDPPacket = this.prepare_deserialize(receive_data);

                if (receiveUDPPacket.getType() == UDP_TYPE.ACK){
                    condition = false;
                }
            }

            catch (SocketTimeoutException e) {}
        }
    }


    public UDPPacket receive() throws SocketException, IOException, ClassNotFoundException{

        UDPPacket result = null;
        boolean condition = true;
        UDPAckPacket udpAckPacket = null;

        DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        DatagramPacket ackPacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        this.socket.setSoTimeout(RECEIVE_TIMEOUT);

        while (condition){

            try{

                this.socket.receive(receivePacket);
                byte[] receive_data = receivePacket.getData();
                UDPPacket receiveUDPPacket = this.prepare_deserialize(receive_data);

                if (udpAckPacket == null){

                    udpAckPacket = new UDPAckPacket();
                    this.socket.connect(receivePacket.getAddress(), receivePacket.getPort());

                    result = receiveUDPPacket;
                    result.setSender(receivePacket.getAddress().getHostAddress());
                    result.setReceiver(this.socket.getLocalAddress().getHostAddress());
                }

                byte[] ack_data = this.prepare_serialize(udpAckPacket);
                ackPacket.setData(ack_data);
                this.socket.send(ackPacket);
            }

            catch (SocketTimeoutException e){
                condition = false;
                this.socket.disconnect();
            }
        }

        return result;
    }
}