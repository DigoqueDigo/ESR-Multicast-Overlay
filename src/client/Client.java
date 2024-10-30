package client;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import service.gather.BootstrapperGrather;


public class Client {

    public static void main(String[] args) throws InterruptedException, IOException{

        String nodeName = args[0];
        String bootstrapperIP = args[1];
        
        BootstrapperGrather bootstrapperGrather = new BootstrapperGrather(nodeName,bootstrapperIP);
        JSONObject bootstrapperInfo = bootstrapperGrather.getBootstrapperInfo();
        
        List<String> neighbours = bootstrapperInfo.getJSONArray("neighbours")
            .toList().stream().map(Object::toString).collect(Collectors.toList());
        
        System.out.println(neighbours);
    }
}