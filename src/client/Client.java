package client;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import service.gather.BootstrapperGather;


public class Client {

    public static void main(String[] args) throws InterruptedException, IOException{

        String nodeName = args[0];
        String bootstrapperIP = args[1];
        
        BootstrapperGather bootstrapperGather = new BootstrapperGather(nodeName,bootstrapperIP);
        JSONObject bootstrapperInfo = bootstrapperGather.getBootstrapperInfo();
        
        List<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toList());
        
        System.out.println(neighbours);
    }
}