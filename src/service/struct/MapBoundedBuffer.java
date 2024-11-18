package service.struct;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class MapBoundedBuffer <K,V>{

    private static final int BUFFER_SIZE = 10;
    private ConcurrentHashMap<K,BoundedBuffer<V>> boundedBuffers;


    public MapBoundedBuffer(){
        this.boundedBuffers = new ConcurrentHashMap<>();
    }


    public void addBoundedBuffer(K key){
        this.boundedBuffers.put(key,new BoundedBuffer<V>(BUFFER_SIZE));
    }


    public void removeBoundedBuffer(K key){
        this.boundedBuffers.remove(key);
    }


    public Set<K> getKeys(){
        return this.boundedBuffers.keySet();
    }


    public BoundedBuffer<V> getBoundedBuffer(K key){
        return this.boundedBuffers.get(key);
    }


    public void put(K key, V value){
        try {this.boundedBuffers.get(key).push(value);}
        catch (Exception e) {e.printStackTrace();}
    }


    public boolean containsKey(K key){
        return this.boundedBuffers.containsKey(key);
    }
}