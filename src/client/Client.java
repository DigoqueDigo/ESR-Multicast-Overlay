package client;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import org.json.JSONObject;
import bootstrapper.Bootstrapper;
import service.gather.BootstrapperGather;
import struct.EdgeProviders;


public class Client{

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException{

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        BootstrapperGather bootstrapperGather = new BootstrapperGather(nodeName,bootstrapperIP,Bootstrapper.PORT);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();

        List<String> edgeNodes = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toList());

        EdgeProviders edgeProviders = new EdgeProviders();
        TimerTask clientLinkTimerTask = new ClientLinkTimer(edgeNodes,edgeProviders);
        Timer clientLinkTimer = new Timer();

        clientLinkTimer.schedule(clientLinkTimerTask,ClientLinkTimer.DELAY,ClientLinkTimer.PERIOD);

        ClientUI clientUI = new ClientUI(edgeProviders);
        clientUI.start();
        clientUI.close();

        clientLinkTimerTask.cancel();
        clientLinkTimer.cancel();
    }
}