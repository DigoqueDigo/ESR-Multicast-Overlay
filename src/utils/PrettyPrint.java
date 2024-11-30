package utils;
import java.util.Map;
import java.util.Set;
import com.sarojaba.prettytable4j.PrettyTable;


public final class PrettyPrint{

    public static String toString(Map<String,Set<String>> data, String header1, String header2){

        PrettyTable pt = PrettyTable.fieldNames(header1,header2);

        for (Map.Entry<String,Set<String>> entry : data.entrySet()){
            for (String element : entry.getValue()){
                pt.addRow(entry.getKey(),element);
            }
        }

        return pt.toString();
    }
}