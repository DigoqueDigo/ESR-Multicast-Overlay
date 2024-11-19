package server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import org.json.JSONObject;
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

        BootstrapperGather bootstrapperGather = new BootstrapperGather(serverName,bootstrapperIP);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();

        List<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
        .toList().stream().map(Object::toString).collect(Collectors.toList());

        BoundedBuffer<TCPPacket> inBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<TCPPacket> controlBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<TCPPacket> videoBuffer = new BoundedBuffer<>(10);
        BoundedBuffer<String> connectionBuffer = new BoundedBuffer<>(10);
        MapBoundedBuffer<String,TCPPacket> outBuffers = new MapBoundedBuffer<>();

        Timer timer = new Timer();
        TimerTask controlFlood = new ServerFloodTimer(serverName,videoFolder,outBuffers);

        List<Thread> workers = new ArrayList<>();

        workers.add(new Thread(new WaitEstablishConnection(inBuffer,outBuffers)));
        workers.add(new Thread(new FloodEstablishConnection(connectionBuffer,inBuffer,outBuffers)));

        workers.add(new Thread(new CoreWorker(inBuffer,controlBuffer,videoBuffer)));
        workers.add(new Thread(new ServerFloodControlWorker(controlBuffer,outBuffers)));

        timer.schedule(controlFlood,ServerFloodTimer.DELAY,ServerFloodTimer.PERIOD);

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