package struct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import com.sarojaba.prettytable4j.PrettyTable;


public class VideoProviders{

    private static final double NEW_TIMESTAMP_WEIGHT = 0.2;
    private static final double CURRENT_RATING_WEIGHT = 0.8;
    private static final Long ZOMBIE =  18_000L;

    /* providers
     * videoA -> [(O1,165),(O5,162)]
     * videoB -> [(O2,100)]
     */
    private ReentrantLock lock;
    private Map<String,Long> updates;
    private Map<String,List<Pair<String,Long>>> providers;


    public VideoProviders(){
        this.lock = new ReentrantLock();
        this.updates = new HashMap<>();
        this.providers = new HashMap<>();
    }


    public void addProvider(String video, String provider, Long timestamp){

        try{
            this.lock.lock();
            this.providers.putIfAbsent(video,new ArrayList<>());

            Long oldValue = this.providers.get(video).stream()
                .filter(x -> x.getLeft().equals(provider))
                .map(x -> x.getRight())
                .findFirst().orElse(timestamp);

            timestamp = Math.round(oldValue * CURRENT_RATING_WEIGHT + timestamp * NEW_TIMESTAMP_WEIGHT);
            Pair<String,Long> entry = Pair.of(provider,timestamp);

            this.providers.get(video).removeIf(x -> x.getLeft().equals(provider));
            this.providers.get(video).add(entry);
            this.updates.put(provider,System.currentTimeMillis());
        }

        catch (Exception e) {e.printStackTrace();}

        finally {this.lock.unlock();}
    }


    public void removeProvider(String provider){

        try{

            this.lock.lock();
            this.updates.remove(provider);

            for (List<Pair<String,Long>> providerList : this.providers.values()){
                providerList.removeIf(x -> x.getLeft().equals(provider));
            }
        }

        catch (Exception e) {e.printStackTrace();}

        finally {this.lock.unlock();}
    }


    public void clear(){

        try{
            this.lock.lock();
            this.updates.clear();
            this.providers.clear();
        }

        catch (Exception e) {e.printStackTrace();}

        finally {this.lock.unlock();}
    }


    public Set<String> getVideos(){

        try{
            this.lock.lock();
            return new HashSet<>(this.providers.keySet());
        }

        catch (Exception e){
            e.printStackTrace();
            return new HashSet<>();
        }

        finally {this.lock.unlock();}
    }


    public Set<String> getAvailableVideos(){

        try{
            this.lock.lock();
            return this.providers.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 0)
                .map(entry -> entry.getKey())
                .collect(Collectors.toSet());
        }

        catch (Exception e){
            e.printStackTrace();
            return new HashSet<>();
        }

        finally {this.lock.unlock();}
    }


    public Set<String> getProviders(String video){

        try{

            this.lock.lock();
            Set<String> videoProviders = new HashSet<>();

            if (this.providers.containsKey(video)){
                for (Pair<String,Long> videoProvider : this.providers.get(video)){
                    videoProviders.add(videoProvider.getLeft());
                }
            }

            return videoProviders;
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        finally {this.lock.unlock();}
    }


    public String getBestProvider(String video){

        try{
            this.lock.lock();
            String bestProvider = null;

            if (this.providers.containsKey(video)){
                bestProvider = this.providers.get(video).stream()
                    .sorted(Comparator.comparingLong(x -> x.getRight()))
                    .map(Pair::getLeft)
                    .findFirst()
                    .orElse(null);
            }

            return bestProvider;
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        finally {this.lock.unlock();}
    }


    public Long getProviderTime(String video, String provider){

        try{

            this.lock.lock();
            Long time = null;

            for (Pair<String,Long> entry : this.providers.get(video)){
                if (entry.getLeft().equals(provider)){
                    time = entry.getRight();
                    break;
                }
            }

            return time;
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        finally {this.lock.unlock();}
    }


    public void deleteZombies(){

        try{
            this.lock.lock();

            Set<String> zombies = this.updates.entrySet().stream()
                .filter(x -> System.currentTimeMillis() - x.getValue() > ZOMBIE)
                .map(x -> x.getKey())
                .collect(Collectors.toSet());

            for (String zombie : zombies){
                this.removeProvider(zombie);
            }
        }

        catch (Exception e) {e.printStackTrace();}

        finally {this.lock.unlock();}
    }


    public String toString(){

        try{
            this.lock.lock();

            PrettyTable pt = PrettyTable.fieldNames("Video","Provider","Delay");

            for (Map.Entry<String,List<Pair<String,Long>>> entry : this.providers.entrySet()){
                for (Pair<String,Long> provider : entry.getValue()){
                    pt.addRow(entry.getKey(),provider.getLeft(),provider.getRight());
                }
            }

            pt.comma(true);
            return pt.toString();
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        finally {this.lock.unlock();}
    }
}