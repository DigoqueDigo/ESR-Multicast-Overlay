package node;
import org.json.JSONObject;
import packet.tcp.TCPPacket;
import service.core.CoreWorker;
import service.core.control.ControlWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;
import service.core.struct.Parents;
import service.establishconnection.ClientWaitEstablishConnection;
import service.establishconnection.FloodEstablishConnection;
import service.establishconnection.WaitEstablishConnection;
import service.gather.BootstrapperGather;
import java.io.IOException;
import java.util.ArrayList;
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

        boolean isEdge = bootstrapperInfo.getBoolean("edge");
        System.out.println("EDGE STATUS: " + isEdge);

        BoundedBuffer<TCPPacket> inBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<TCPPacket> controlBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<String> connectionBuffer = new BoundedBuffer<>(10);

        Parents parents = new Parents();
        OutBuffers outBuffers = new OutBuffers();

        List<Thread> workers = new ArrayList<>();

        workers.add(new Thread(new WaitEstablishConnection(inBuffer,outBuffers)));
        workers.add(new Thread(new FloodEstablishConnection(connectionBuffer,inBuffer,outBuffers)));

        workers.add(new Thread(new CoreWorker(inBuffer,controlBuffer)));
        workers.add(new Thread(new ControlWorker(nodeName,parents,controlBuffer,connectionBuffer,outBuffers)));

        if (isEdge){
            workers.add(new Thread(new ClientWaitEstablishConnection()));
        }

        for (Thread worker : workers){
            worker.start();
        }

        for (String neighbour : neighbours){
            connectionBuffer.push(neighbour);
        }

        for (Thread worker : workers){
            worker.join();
        }
    }
}