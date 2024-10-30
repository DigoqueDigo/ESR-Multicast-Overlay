package service.core.struct;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class Parents{

    private ConcurrentMap<String,Long> parentsTimestamp;


    public Parents(){
        this.parentsTimestamp = new ConcurrentHashMap<>();
    }


    public void addParent(String parent, Long timestamp){
        this.parentsTimestamp.put(parent,timestamp);
    }


    public void removeParent(String parent){
        this.parentsTimestamp.remove(parent);
    }


    public void clear(){
        this.parentsTimestamp.clear();
    }


    public String getBestParent(){
        return this.parentsTimestamp.entrySet().stream()
            .min(Comparator.comparingLong(x -> x.getValue()))
            .map(x -> x.getKey())
            .orElse(null);  
    }
}