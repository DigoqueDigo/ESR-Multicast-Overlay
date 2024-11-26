package struct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import com.sarojaba.prettytable4j.PrettyTable;


public class VideoCurrentProviders{

    /*
     * tabela com o atual provider do video
     * video1 -> O1
     * video2 -> O3
     */
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


    public String toString(){

        try{
            this.lock.lock();

            PrettyTable pt = PrettyTable.fieldNames("Video","Provider");

            for (Map.Entry<String,String> entry : this.currentProviders.entrySet()){
                pt.addRow(entry.getKey(),entry.getValue());
            }

            return pt.toString();
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        finally{
            this.lock.unlock();
        }
    }
}