package service.core.struct;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class VideoConsumers{

    /*
     * tabela de que pediu um video
     * video1 -> [O1, O2]
     * video2 -> [O1]
     */
    ConcurrentMap<String,Set<String>> table;


    public VideoConsumers(){
        this.table = new ConcurrentHashMap<>();
    }


    public void put(String video, String node){
        this.table.putIfAbsent(video,new HashSet<>());
        this.table.get(video).add(node);
    }


    public void remove(String video, String node){
        this.table.get(video).remove(node);
        if (this.table.get(video).size() == 0)
            this.table.remove(video);
    }


    public Set<String> getNodes(String video){
        return this.table.get(video);
    }
}