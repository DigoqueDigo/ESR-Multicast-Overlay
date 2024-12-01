package struct;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.sarojaba.prettytable4j.PrettyTable;


public class EdgeProviders{

    private ReentrantLock lock;
    private Table<String,String,Float> edgeTable;


    public EdgeProviders(){
        this.lock = new ReentrantLock();
        this.edgeTable = HashBasedTable.create();
    }


    private void put(String row, String column, Float value){

        try{
            this.lock.lock();
            this.edgeTable.put(row,column,value);
        }

        catch (Exception e){
            e.printStackTrace();
        }

        finally{
            this.lock.unlock();
        }
    }


    public void addLoss(String edgeNode, Float loss){
        this.put(edgeNode,"loss",loss);
    }


    public void addRTT(String edgeNode, Float rtt){
        this.put(edgeNode,"rtt",rtt);
    }


    public Set<String> rowKeySet(){

        try{
            this.lock.lock();
            return new HashSet<>(this.edgeTable.rowKeySet());
        }

        catch (Exception e){
            e.printStackTrace();
            return new HashSet<>();
        }

        finally{
            this.lock.unlock();
        }
    }


    public String toString(){

        try{
            this.lock.lock();
            PrettyTable pt = PrettyTable.fieldNames("EdgeNode","Loss","RTT");

            for (String edgeNode : this.edgeTable.rowKeySet()){
                Float loss = this.edgeTable.get(edgeNode,"loss");
                Float delay = this.edgeTable.get(edgeNode,"rtt");
                pt.addRow(edgeNode,loss,delay);
            }

            pt.comma(true);
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