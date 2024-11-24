package struct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


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
            consumers = this.consumers.get(video);
        }
        return consumers;
    }


    public Set<String> getVideos(){
        return this.consumers.keySet();
    }


    public boolean containsKey(String video){
        return this.consumers.containsKey(video);
    }


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("----- VIDEO CONSUMERS -----");
        buffer.append(this.consumers.entrySet().stream()
            .map(x -> x.getKey() + " :: " + x.getValue())
            .collect(Collectors.joining("\n","\n","")));
        return buffer.toString();
    }
}