package node;
import org.json.JSONObject;
import packet.tcp.TCPPacket;
import service.core.ControlWorker;
import service.core.CoreWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;
import service.core.struct.Parents;
import service.establishconnection.FloodEstablishConnection;
import service.establishconnection.WaitEstablishConnection;
import service.gather.BootstrapperGather;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class Node {

    public static final int PORT = 3000;

    public static void main(String[] args) throws InterruptedException, IOException {

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        BootstrapperGather bootstrapperGather = new BootstrapperGather(nodeName,bootstrapperIP);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();

        List<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toList());

        BoundedBuffer<TCPPacket> inBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<TCPPacket> controlBuffer = new BoundedBuffer<>(10);

        Parents parents = new Parents();
        OutBuffers outBuffers = new OutBuffers();

        Thread waitEstablishConnection = new Thread(new WaitEstablishConnection(inBuffer,outBuffers));
        Thread floodEstablishConnection = new Thread(new FloodEstablishConnection(inBuffer,outBuffers,neighbours));

        Thread coreWorker = new Thread(new CoreWorker(inBuffer,controlBuffer));
        Thread controlWorker = new Thread(new ControlWorker(nodeName,parents,controlBuffer,outBuffers));

        waitEstablishConnection.start();
        floodEstablishConnection.start();
        coreWorker.start();
        controlWorker.start();

        waitEstablishConnection.join();
        floodEstablishConnection.join();
        coreWorker.join();
        controlWorker.join();
    }
}