package utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public final class IO{

    public static int readAllBytes(InputStream inputStream, byte[] data, int length) throws IOException{

        int  attempt = 1;
        int bytes_read = 0;

        for (int rest = length; attempt > 0 && bytes_read < length; rest -= attempt){

            attempt = inputStream.read(data,bytes_read,rest);
            if (attempt > 0) bytes_read += attempt;
        }

        return bytes_read;
    }


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


    public static int mkfifo(String filename) throws IOException, InterruptedException{
        ProcessBuilder processBuilder = new ProcessBuilder("mkfifo", filename);
        Process process = processBuilder.start();
        return process.waitFor();
    }


    public static int rm(String filename) throws IOException, InterruptedException{
        ProcessBuilder processBuilder = new ProcessBuilder("rm", filename);
        Process process = processBuilder.start();
        return process.waitFor();
    }
}