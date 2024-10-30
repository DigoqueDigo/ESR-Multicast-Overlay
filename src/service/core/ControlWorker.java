package service.core;
import java.util.HashSet;
import java.util.Set;
import packet.tcp.TCPFloodPacket;
import packet.tcp.TCPPacket;
import packet.tcp.TCPPacket.TYPE;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;
import service.core.struct.Parents;


public class ControlWorker implements Runnable{

    private Parents parents;
    private Set<Long> timestampHistory;
    private OutBuffers outBuffers;
    private BoundedBuffer<TCPPacket> controlBuffer;


    public ControlWorker(Parents parents, BoundedBuffer<TCPPacket> controlBuffer, OutBuffers outBuffers){
        this.parents = parents;
        this.outBuffers = outBuffers;
        this.controlBuffer = controlBuffer;
        this.timestampHistory = new HashSet<>();
    }


    private void handleFloodPacket(TCPFloodPacket tcpFloodPacket){

        long serverTimeStamp = tcpFloodPacket.getTimestamp();
        long delay = System.nanoTime() - serverTimeStamp;

        this.parents.addParent(tcpFloodPacket.getSender(),delay);
        this.timestampHistory.add(serverTimeStamp);

        // Se nunca tiver visto este servertimestamp vou dar flood dele
        if (this.timestampHistory.contains(serverTimeStamp) == false){
            this.outBuffers.sendToAll(tcpFloodPacket);
        }

        // o historico do servertimestamp está sempre a crescer
        // arranjar forma de eliminar alguns valores
    }


    private void handleGrandFatherPacket(TCPPacket tcpPacket){

    }


    public void run(){

        try{

            TCPPacket tcpPacket;

            while ((tcpPacket = this.controlBuffer.pop()) != null){

                if (tcpPacket.getType() == TYPE.CONTROL_FLOOD){
                    this.handleFloodPacket((TCPFloodPacket)tcpPacket);
                }

                else if (tcpPacket.getType() == TYPE.CONTROL_GRANDFATHER){
                    this.handleGrandFatherPacket(tcpPacket);
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}