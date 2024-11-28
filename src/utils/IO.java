package utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public final class IO{

    public static List<String> listFiles(String folder){

        List<String> files = new ArrayList<>();
        File directory = new File(folder);

        if (directory.exists() && directory.isDirectory()){
            for (File file : directory.listFiles()){
                if (file.isFile()){
                    files.add(file.getName());
                }
            }
        }

        return files;
    }
}