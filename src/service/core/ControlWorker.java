package service.core;
import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import packet.tcp.TCPFloodControlPacket;
import packet.tcp.TCPPacket;
import packet.tcp.TCPPacket.TYPE;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;
import service.core.struct.Parents;


public class ControlWorker implements Runnable{

    private static final int HISTORY_SIZE = 100;

    private String signature;
    private Parents parents;
    private OutBuffers outBuffers;
    private Queue<String> history;
    private BoundedBuffer<TCPPacket> controlBuffer;


    public ControlWorker(String signature, Parents parents, BoundedBuffer<TCPPacket> controlBuffer, OutBuffers outBuffers){
        this.signature = signature;
        this.parents = parents;
        this.outBuffers = outBuffers;
        this.controlBuffer = controlBuffer;
        this.history = new CircularFifoQueue<>(HISTORY_SIZE);
    }


    private void handleFloodPacket(TCPFloodControlPacket tcpFloodPacket){

        // so inspeciono o pacote se nao tiver a minha assinatura
        if (tcpFloodPacket.getSignatures().contains(signature) == false){

            String sender = tcpFloodPacket.getSender();
            Long serverTimeStamp = tcpFloodPacket.getTimestamp();
            long delay = System.nanoTime() - serverTimeStamp;

            // so adiciono o nodo como pai se for o primeiro pacote que recebo dele
            String identifier = sender + serverTimeStamp.toString();

            if (this.history.contains(identifier) == false){
                this.history.add(identifier);
                this.parents.addParent(tcpFloodPacket.getSender(),delay);
            }

            // adicionar a minha assinatura ao pacote de control flood
            tcpFloodPacket.addSignature(signature);

            // enviar o pacote para todas as interfaces execeto a do sender
            for (String neighbour : this.outBuffers.getKeys()){
                if (neighbour.equals(sender) == false){
                    this.outBuffers.addPacket(neighbour,tcpFloodPacket);
                    System.out.println("ControlWorker send: " + signature + " -> " + neighbour);
                }
            }
        }

        System.out.println(this.parents);
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