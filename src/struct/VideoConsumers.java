package struct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.sarojaba.prettytable4j.PrettyTable;


public class VideoConsumers{

    /*
     * tabela de que pediu um video
     * video1 -> [O1, O2]
     * video2 -> [O1]
     */
    Map<String,Set<String>> consumers;


    public VideoConsumers(){
        this.consumers = new HashMap<>();
    }


    public void put(String video, String node){
        this.consumers.putIfAbsent(video,new HashSet<>());
        this.consumers.get(video).add(node);
    }


    public void remove(String video, String node){
        this.consumers.get(video).remove(node);
        if (this.consumers.get(video).size() == 0){
            this.consumers.remove(video);
        }
    }


    public Set<String> getConsumers(String video){
        Set<String> consumers = new HashSet<>();
        if (this.consumers.containsKey(video)){
            consumers.addAll(this.consumers.get(video));
        }
        return consumers;
    }


    public Set<String> getVideos(){
        return new HashSet<>(this.consumers.keySet());
    }


    public boolean containsKey(String video){
        return this.consumers.containsKey(video);
    }


    public String toString(){

        PrettyTable pt = PrettyTable.fieldNames("Video", "Consumer");

        for (Map.Entry<String,Set<String>> entry : this.consumers.entrySet()){
            for (String consumer : entry.getValue()){
                pt.addRow(entry.getKey(),consumer);
            }
        }

        return pt.toString();
    }
}