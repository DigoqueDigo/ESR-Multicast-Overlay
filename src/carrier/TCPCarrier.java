package carrier;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class TCPCarrier{

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;


    public TCPCarrier(InputStream inputStream, OutputStream outputStream){
        this.dataInputStream = new DataInputStream(inputStream);
        this.dataOutputStream = new DataOutputStream(outputStream);
    }


    public void send(byte[] data) throws IOException{
        this.dataOutputStream.writeInt(data.length);
        this.dataOutputStream.write(data);
        this.dataOutputStream.flush();
    }


    public byte[] receive() throws IOException{

        int data_size = this.dataInputStream.readInt();
        byte data[] = new byte[data_size];

        if (this.readAllBytes(dataInputStream,data,data_size) != data_size){
            throw new IOException("TCP packet reading incomplete");
        }

        return data;
    }


    public int readAllBytes(InputStream inputStream, byte[] data, int length) throws IOException{

        int  attempt = 1;
        int bytes_read = 0;

        for (int rest = length; attempt > 0 && bytes_read < length; rest -= attempt){

            attempt = inputStream.read(data,bytes_read,rest);
            if (attempt > 0) bytes_read += attempt;
        }

        return bytes_read;
    }
}