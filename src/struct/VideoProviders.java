package struct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;


public class VideoProviders{

    private static final double NEW_TIMESTAMP_WEIGHT = 0.25;
    private static final double CURRENT_RATING_WEIGHT = 0.75;

    // o fornecedor é dado como morto se não for atualizado em 20 segundos
    private static final Long ZOMBIE =  20_000_000_000L;

    /* providers
     * videoA -> [(O1,165),(O5,162)]
     * videoB -> [(O2,100)]
     */
    private ConcurrentMap<String,List<Pair<String,Long>>> providers;
    private ConcurrentMap<String,Long> updates;


    public VideoProviders(){
        this.providers = new ConcurrentHashMap<>();
        this.updates = new ConcurrentHashMap<>();
    }


    public void addProvider(String video, String provider, Long timestamp){

        this.providers.putIfAbsent(video,new ArrayList<>());

        Long oldValue = this.providers.get(video).stream()
            .filter(x -> x.getLeft().equals(provider))
            .map(x -> x.getRight())
            .findFirst().orElse(timestamp);

        timestamp = Math.round(oldValue * CURRENT_RATING_WEIGHT + timestamp * NEW_TIMESTAMP_WEIGHT);
        Pair<String,Long> entry = Pair.of(provider,timestamp);

        this.providers.get(video).removeIf(x -> x.getLeft().equals(provider));
        this.providers.get(video).add(entry);
        this.updates.put(provider,System.nanoTime());
    }


    public void removeProvider(String provider){
        this.providers.values().stream()
            .forEach(providers -> providers.removeIf(x -> x.getLeft().equals(provider)));
        this.updates.remove(provider);
    }


    public void clear(){
        this.providers.clear();
        this.updates.clear();
    }


    public Set<String> getVideos(){
        return this.providers.keySet();
    }


    public Set<String> getProviders(String video){
        return this.providers.containsKey(video) ?
            this.providers.get(video).stream()
                .map(Pair::getLeft)
                .collect(Collectors.toSet()) :
            new HashSet<>();
    }


    public String getBestProvider(String video){
        return this.providers.get(video).stream()
            .sorted(Comparator.comparingLong(x -> x.getRight()))
            .map(Pair::getLeft)
            .findFirst().orElse(null);
    }


    public void deleteZombies(){
        this.updates.entrySet().stream()
            .filter(x -> System.nanoTime() - x.getValue() > ZOMBIE)
            .forEach(x -> this.removeProvider(x.getKey()));
    }


    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append("VIDEO PROVIDERS (TIMESTAMPS)");
        this.providers.entrySet().stream().forEach(x -> buffer.append("\n" + x.getKey() + "\t" + x.getValue()));
        return buffer.toString();
    }
}