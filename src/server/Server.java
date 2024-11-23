package server;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import org.json.JSONObject;
import bootstrapper.Bootstrapper;
import packet.tcp.TCPPacket;
import service.core.CoreWorker;
import service.establishconnection.FloodEstablishConnection;
import service.establishconnection.WaitEstablishConnection;
import service.gather.BootstrapperGather;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;


public class Server {

    public static void main(String[] args) throws InterruptedException, IOException{

        String serverName = args[0];
        String videoFolder = args[1];
        String bootstrapperIP = args[2];

        BootstrapperGather bootstrapperGather = new BootstrapperGather(serverName,bootstrapperIP,Bootstrapper.PORT);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();

        Set<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toSet());

        BoundedBuffer<TCPPacket> inBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<TCPPacket> controlBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<TCPPacket> videoBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<String> connectionBuffer = new BoundedBuffer<>(10);

        MapBoundedBuffer<String,TCPPacket> outBuffers = new MapBoundedBuffer<>();

        TimerTask serverFloodTimer = new ServerFloodTimer(serverName,videoFolder,outBuffers);

        Set<Thread> workers = new HashSet<>();

        workers.add(new Thread(new WaitEstablishConnection(inBuffer,outBuffers)));
        workers.add(new Thread(new FloodEstablishConnection(connectionBuffer,inBuffer,outBuffers)));

        workers.add(new Thread(new CoreWorker(inBuffer,controlBuffer,videoBuffer)));
        workers.add(new Thread(new ServerFloodControlWorker(controlBuffer,outBuffers)));
        workers.add(new Thread(new ServerVideoControlWorker(videoFolder,videoBuffer,outBuffers)));

        new Timer().schedule(serverFloodTimer,ServerFloodTimer.DELAY,ServerFloodTimer.PERIOD);

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