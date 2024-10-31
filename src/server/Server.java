package server;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import org.json.JSONObject;
import packet.tcp.TCPPacket;
import service.core.ControlFloodTimer;
import service.core.ControlWorker;
import service.core.CoreWorker;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;
import service.core.struct.Parents;
import service.establishconnection.FloodEstablishConnection;
import service.establishconnection.WaitEstablishConnection;
import service.gather.BootstrapperGather;


public class Server {

    public static void main(String[] args) throws InterruptedException, IOException{

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

        Timer timer = new Timer();
        TimerTask controlFlood = new ControlFloodTimer(nodeName,outBuffers);

        Thread waitEstablishConnection = new Thread(new WaitEstablishConnection(inBuffer,outBuffers));
        Thread floodEstablishConnection = new Thread(new FloodEstablishConnection(inBuffer,outBuffers,neighbours));

        Thread coreWorker = new Thread(new CoreWorker(inBuffer,controlBuffer));
        Thread controlWorker = new Thread(new ControlWorker(parents,controlBuffer,outBuffers));

        waitEstablishConnection.start();
        floodEstablishConnection.start();
        coreWorker.start();
        controlWorker.start();
        timer.schedule(controlFlood,ControlFloodTimer.delay,ControlFloodTimer.period);

        waitEstablishConnection.join();
        floodEstablishConnection.join();
        coreWorker.join();
        controlWorker.join();
    }
}