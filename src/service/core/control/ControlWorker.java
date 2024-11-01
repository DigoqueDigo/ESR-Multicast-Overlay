package service.core.control;
import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import packet.tcp.TCPConnectionStatePacket;
import packet.tcp.TCPFloodControlPacket;
import packet.tcp.TCPGrandfatherControlPacket;
import packet.tcp.TCPPacket;
import packet.tcp.TCPConnectionStatePacket.PROTOCOL;
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
            // so dou flood do pacote se for o primerio que recebo naquela interface
            String identifier = sender + serverTimeStamp.toString();

            if (this.history.contains(identifier) == false){

                this.history.add(identifier);
                this.parents.addParent(tcpFloodPacket.getSender(),delay);

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
        }

        // eliminar todos os pais que sejam zombies
        // podia fazer isto com o timertask, mas até que ponto compensa
        this.parents.getZombies().stream().forEach(x -> this.parents.removeParent(x));

        System.out.println(this.parents);
    }


    private void handleGrandFatherPacket(TCPGrandfatherControlPacket tcpGrandfatherPacket){

    }


    private void handleConnectionLost(TCPConnectionStatePacket tcpStatePacket){
        String neighbour = tcpStatePacket.getSender();
        this.outBuffers.addPacket(neighbour,tcpStatePacket);
        this.parents.removeParent(neighbour);
        this.outBuffers.removeOutBuffer(neighbour);
        System.out.println("ControlWorker remove neighbour: " + neighbour);
    }


    private void handleConnectionStatePacket(TCPConnectionStatePacket tcpStatePacket){
        if (tcpStatePacket.getProtocol() == PROTOCOL.CONNECTION_LOST){
            this.handleConnectionLost(tcpStatePacket);
        }
    }


    public void run(){

        try{

            TCPPacket tcpPacket;

            while ((tcpPacket = this.controlBuffer.pop()) != null){

                switch (tcpPacket.getType()){

                    case CONTROL_FLOOD:
                        this.handleFloodPacket((TCPFloodControlPacket) tcpPacket);
                        break;

                    case CONTROL_GRANDFATHER:
                        this.handleGrandFatherPacket((TCPGrandfatherControlPacket) tcpPacket);
                        break;

                    case CONNECTION_STATE:
                        this.handleConnectionStatePacket((TCPConnectionStatePacket) tcpPacket);
                        break;

                    default:
                        System.out.println("ControlWorker unknown tcpPacket: " + tcpPacket);
                        break;
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}