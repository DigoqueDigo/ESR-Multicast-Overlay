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
import packet.udp.UDPVideoListPacket;
import packet.udp.UDPPacket.UDP_TYPE;
import utils.Crypto;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;


public class UDPCarrier{

    private static final int BUFFER_SIZE = 4096;
    private static final int SEND_TIMEOUT = 20;
    private static final int RECEIVE_TIMEOUT = 500;
    private static Map<Class<?>,Function<byte[],UDPPacket>> deserializeMap = new HashMap<>();

    static{
        // Adicionar um metodo de deserialize por extensao de TCPPacket
        deserializeMap.put(UDPAckPacket.class, x -> UDPAckPacket.deserialize(x));
        deserializeMap.put(UDPVideoListPacket.class, x -> UDPVideoListPacket.deserialize(x));
        deserializeMap.put(UDPVideoControlPacket.class, x -> UDPVideoControlPacket.deserialize(x));
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


    private byte[] serialize(UDPPacket udpPacket) throws Exception{

        byte[] serialize = udpPacket.serialize();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeUTF(udpPacket.getClass().getCanonicalName());
        dataOutputStream.writeInt(serialize.length);
        dataOutputStream.write(serialize);
        dataOutputStream.flush();

        byte[] plainText = byteArrayOutputStream.toByteArray();
        byte[] crypto = Crypto.encrypt(plainText);

        byteArrayOutputStream.reset();
        dataOutputStream.writeInt(crypto.length);
        dataOutputStream.write(crypto);
        dataOutputStream.flush();

        byte[] result = byteArrayOutputStream.toByteArray();
        dataOutputStream.close();
        byteArrayOutputStream.close();

        return result;
    }


    private UDPPacket deserialize(byte[] encrypt) throws Exception{

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encrypt);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        int crypto_size = dataInputStream.readInt();
        byte[] crypto = new byte[crypto_size];

        dataInputStream.readFully(crypto);
        dataInputStream.close();
        byteArrayInputStream.close();

        byte[] plaintext = Crypto.decrypt(crypto);
        byteArrayInputStream = new ByteArrayInputStream(plaintext);
        dataInputStream = new DataInputStream(byteArrayInputStream);

        Class<?> clazz = Class.forName(dataInputStream.readUTF());
        int deserialize_size = dataInputStream.readInt();
        byte[] deserialize = new byte[deserialize_size];

        dataInputStream.readFully(deserialize);
        UDPPacket udpPacket = deserializeMap.get(clazz).apply(deserialize);

        dataInputStream.close();
        byteArrayInputStream.close();

        return udpPacket;
    }


    public void send(UDPPacket udpPacket) throws Exception{

        boolean condition = true;
        byte[] data = this.serialize(udpPacket);

        DatagramPacket sendPacket = new DatagramPacket(data, data.length);
        DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        this.socket.setSoTimeout(SEND_TIMEOUT);

        while (condition){

            this.socket.send(sendPacket);

            try{

                this.socket.receive(receivePacket);
                byte[] receive_data = receivePacket.getData();
                UDPPacket receiveUDPPacket = this.deserialize(receive_data);

                if (receiveUDPPacket.getType() == UDP_TYPE.ACK && receiveUDPPacket.getID() == udpPacket.getID()){
                    condition = false;
                    System.out.println("UDPCarrier: receive ACK");
                }
            }

            catch (SocketTimeoutException e) {}
        }
    }


    public UDPPacket receive() throws Exception{

        boolean condition = true;
        UDPPacket udpResultPacket = null;
        UDPAckPacket udpAckPacket = null;

        DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        DatagramPacket ackPacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        this.socket.setSoTimeout(RECEIVE_TIMEOUT);

        while (condition){

            try{

                this.socket.receive(receivePacket);
                byte[] receive_data = receivePacket.getData();
                UDPPacket receiveUDPPacket = this.deserialize(receive_data);

                if (udpResultPacket == null && udpAckPacket == null){

                    udpResultPacket = receiveUDPPacket;

                    udpResultPacket.setSenderIP(receivePacket.getAddress().getHostAddress());
                    udpResultPacket.setSenderPort(receivePacket.getPort());

                    udpResultPacket.setReceiverIP(this.socket.getLocalAddress().getHostAddress());
                    udpResultPacket.setReceiverPort(this.socket.getLocalPort());

                    udpAckPacket = new UDPAckPacket();
                    udpAckPacket.setID(udpResultPacket.getID());
                    this.socket.connect(receivePacket.getAddress(), receivePacket.getPort());
                }

                if (receiveUDPPacket.getID() == udpResultPacket.getID()){
                    ackPacket.setData(this.serialize(udpAckPacket));
                    this.socket.send(ackPacket);
                    System.out.println("UDPCarrrier: send ACK");
                }

                else{
                    condition = false;
                    this.socket.disconnect();
                }
            }

            catch (SocketTimeoutException e){
                condition = false;
                this.socket.disconnect();
            }
        }

        return udpResultPacket;
    }
}