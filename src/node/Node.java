package node;
import org.json.JSONObject;
import service.core.struct.Buffers;
import service.establishconnection.FloodEstablishConnection;
import service.establishconnection.WaitEstablishConnection;
import service.gather.BootstrapperGrather;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class Node {

    public static final int PORT = 3000;

    public static void main(String[] args) throws InterruptedException, IOException {

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        BootstrapperGrather bootstrapperGrather = new BootstrapperGrather(nodeName,bootstrapperIP);
        JSONObject bootstrapperInfo = bootstrapperGrather.getBootstrapperInfo();

        List<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toList());

        Buffers buffers = new Buffers();
        Thread waitEstablishConnection = new Thread(new WaitEstablishConnection(buffers));
        Thread floodEstablishConnection = new Thread(new FloodEstablishConnection(buffers,neighbours));

        waitEstablishConnection.start();
        floodEstablishConnection.start();

        waitEstablishConnection.wait();
        floodEstablishConnection.wait();
    }
}