package node;
import org.json.JSONObject;
import bootstrapper.Bootstrapper;
import node.stream.NodeStreamWaitClient;
import packet.tcp.TCPPacket;
import service.core.CoreWorker;
import service.establishconnection.FloodEstablishConnection;
import service.establishconnection.WaitEstablishConnection;
import service.gather.BootstrapperGather;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;
import struct.VideoCurrentProviders;
import struct.VideoProviders;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;


public class Node {

    public static void main(String[] args) throws InterruptedException, IOException {

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        BootstrapperGather bootstrapperGather = new BootstrapperGather(nodeName,bootstrapperIP,Bootstrapper.PORT);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();

        Set<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toSet());

        boolean isEdge = bootstrapperInfo.getBoolean("edge");

        BoundedBuffer<TCPPacket> inBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<TCPPacket> controlBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<TCPPacket> videoBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<String> connectionBuffer = new BoundedBuffer<>(10);

        MapBoundedBuffer<String,byte[]> streamBuffers = new MapBoundedBuffer<>();
        MapBoundedBuffer<String,TCPPacket> outBuffers = new MapBoundedBuffer<>();

        VideoProviders videoProviders = new VideoProviders();
        VideoCurrentProviders videoCurrentProviders = new VideoCurrentProviders(); 

        TimerTask nodeZombieTimer  = new NodeZombieTimer(videoProviders);
        TimerTask nodeSwitchTimer = new NodeSwitchTimer(videoProviders,videoCurrentProviders,outBuffers);

        Set<Thread> workers = new HashSet<>();

        workers.add(new Thread(new WaitEstablishConnection(inBuffer,outBuffers)));
        workers.add(new Thread(new FloodEstablishConnection(connectionBuffer,inBuffer,outBuffers)));

        workers.add(new Thread(new CoreWorker(inBuffer,controlBuffer,videoBuffer)));
        workers.add(new Thread(new NodeFloodControlWorker(nodeName,videoProviders,controlBuffer,connectionBuffer,outBuffers)));
        workers.add(new Thread(new NodeVideoControlWorker(videoProviders,videoCurrentProviders,videoBuffer,streamBuffers,outBuffers)));

        new Timer().schedule(nodeSwitchTimer,NodeSwitchTimer.DELAY,NodeSwitchTimer.PERIOD);
        new Timer().schedule(nodeZombieTimer,NodeZombieTimer.DELAY,NodeZombieTimer.PERIOD);

        if (isEdge){
            workers.add(new Thread(new NodeStreamWaitClient(videoProviders,inBuffer,streamBuffers)));
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