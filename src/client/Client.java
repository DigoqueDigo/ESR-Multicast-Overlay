package client;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import bootstrapper.Bootstrapper;
import service.gather.BootstrapperGather;


public class Client{

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException{

        String nodeName = args[0];
        String bootstrapperIP = args[1];

        BootstrapperGather bootstrapperGather = new BootstrapperGather(nodeName,bootstrapperIP,Bootstrapper.PORT);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();

        List<String> edgeNodes = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toList());

        ClientUI clientUI = new ClientUI(edgeNodes);
        clientUI.start();
        clientUI.close();
    }
}