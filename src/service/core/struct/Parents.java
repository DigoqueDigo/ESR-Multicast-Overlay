package service.core.struct;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


public class Parents{

    // o pai é dado como morto se não for atualizado em 15 segundos
    private static final Long ZOMBIE =  15_000_000_000L;

    private ConcurrentMap<String,Long> parentsTimestamp;
    private ConcurrentMap<String,Long> parentsUpdate;


    public Parents(){
        this.parentsTimestamp = new ConcurrentHashMap<>();
        this.parentsUpdate = new ConcurrentHashMap<>();
    }


    public void addParent(String parent, Long timestamp){
        this.parentsTimestamp.put(parent,timestamp);
        this.parentsUpdate.put(parent,System.nanoTime());
    }


    public void removeParent(String parent){
        this.parentsTimestamp.remove(parent);
        this.parentsUpdate.remove(parent);
    }


    public void clear(){
        this.parentsTimestamp.clear();
        this.parentsUpdate.clear();
    }


    public int size(){
        return this.parentsTimestamp.size();
    }


    public List<String> getParents(){
        return this.parentsTimestamp.keySet()
            .stream().collect(Collectors.toList());
    }


    public List<String> getZombies(){
        return this.parentsUpdate.entrySet().stream()
            .filter(x -> System.nanoTime() - x.getValue() > ZOMBIE)
            .map(x -> x.getKey()).collect(Collectors.toList());
    }


    public String getBestParent(){
        return this.parentsTimestamp.entrySet().stream()
            .min(Comparator.comparingLong(x -> x.getValue()))
            .map(x -> x.getKey())
            .orElse(null);
    }


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("PARENT MAP (TIMESTAMPS)");
        this.parentsTimestamp.entrySet().stream()
            .forEach(x -> buffer.append("\n" + x.getKey() + "\t" + x.getValue()));
        return buffer.toString();
        }
}