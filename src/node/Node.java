package node;
import org.json.JSONObject;
import packet.tcp.TCPPacket;
import service.core.CoreWorker;
import service.core.control.ControlWorker;
import service.establishconnection.ClientWaitEstablishConnection;
import service.establishconnection.FloodEstablishConnection;
import service.establishconnection.WaitEstablishConnection;
import service.gather.BootstrapperGather;
import service.struct.BoundedBuffer;
import service.struct.MapBoundedBuffer;
import service.struct.VideoProviders;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Node {

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
        BoundedBuffer<TCPPacket> videoBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<String> connectionBuffer = new BoundedBuffer<>(10);

        MapBoundedBuffer<String,TCPPacket> outBuffers = new MapBoundedBuffer<>();
        MapBoundedBuffer<String,byte[]> videoStreams = new MapBoundedBuffer<>();

        VideoProviders parents = new VideoProviders();
    //    VideoTable videoTable = new VideoTable();

        List<Thread> workers = new ArrayList<>();

        workers.add(new Thread(new WaitEstablishConnection(inBuffer,outBuffers)));
        workers.add(new Thread(new FloodEstablishConnection(connectionBuffer,inBuffer,outBuffers)));

        workers.add(new Thread(new CoreWorker(inBuffer,controlBuffer,videoBuffer)));
        workers.add(new Thread(new ControlWorker(nodeName,parents,controlBuffer,connectionBuffer,outBuffers)));
    //    workers.add(new Thread(new StreamControlWorker(videoTable,videoBuffer,videoStreams)));

        if (isEdge){
            workers.add(new Thread(new ClientWaitEstablishConnection(inBuffer,videoStreams)));
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