package struct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;


public class VideoCurrentProviders{

    private ReentrantLock lock;
    private Map<String,String> currentProviders;


    public VideoCurrentProviders(){
        this.lock = new ReentrantLock();
        this.currentProviders = new HashMap<>();
    }


    public void put(String video, String provider){

        try{
            this.lock.lock();
            this.currentProviders.put(video,provider);
        }

        catch (Exception e){
            e.printStackTrace();
        }

        finally{
            this.lock.unlock();
        }
    }


    public String get(String video){

        try{
            this.lock.lock();
            return this.currentProviders.get(video);
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        finally{
            this.lock.unlock();
        }
    }


    public void remove(String video){

        try{
            this.lock.lock();
            this.currentProviders.remove(video);
        }

        catch (Exception e){
            e.printStackTrace();
        }

        finally{
            this.lock.unlock();
        }
    }


    public Set<String> keySet(){

        try{
            this.lock.lock();
            return this.currentProviders.keySet();
        }

        catch (Exception e){
            e.printStackTrace();
            return new HashSet<>();
        }

        finally{
            this.lock.unlock();
        }
    }


    public void lock(){
        this.lock.lock();
    }


    public void unlock(){
        this.lock.unlock();
    }
}