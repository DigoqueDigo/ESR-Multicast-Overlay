package service.core;
import java.util.HashSet;
import java.util.Set;
import packet.tcp.TCPFloodControlPacket;
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


    private void handleFloodPacket(TCPFloodControlPacket tcpFloodPacket){

        long serverTimeStamp = tcpFloodPacket.getTimestamp();
        long delay = System.nanoTime() - serverTimeStamp;

        this.parents.addParent(tcpFloodPacket.getSender(),delay);

        
        // Se nunca tiver visto este servertimestamp vou dar flood dele
        if (this.timestampHistory.contains(serverTimeStamp) == false){
            for (String neighbour : this.outBuffers.getKeys()){
                if (neighbour.equals(tcpFloodPacket.getSender()) == false){
                    this.outBuffers.addPacket(neighbour,tcpFloodPacket);
                    System.out.println("Received packet from" + tcpFloodPacket.getSender() + " and sending it to " + neighbour);
                }
            }
        }

        this.timestampHistory.add(serverTimeStamp);
        System.out.println(this.parents);

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
                    this.handleFloodPacket((TCPFloodControlPacket)tcpPacket);
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