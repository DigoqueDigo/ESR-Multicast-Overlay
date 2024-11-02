package service.core.control;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import packet.tcp.TCPConnectionStatePacket;
import packet.tcp.TCPFloodControlPacket;
import packet.tcp.TCPGrandfatherControlPacket;
import packet.tcp.TCPPacket;
import packet.tcp.TCPConnectionStatePacket.CS_PROTOCOL;
import packet.tcp.TCPGrandfatherControlPacket.GF_PROTOCOL;
import service.core.struct.BoundedBuffer;
import service.core.struct.OutBuffers;
import service.core.struct.Parents;


public class ControlWorker implements Runnable{

    private static final int HISTORY_SIZE = 100;

    private String signature;
    private Parents parents;

    private Queue<String> history;
    private Set<String> blackList;
    private Set<String> grandParents;

    private OutBuffers outBuffers;
    private BoundedBuffer<TCPPacket> controlBuffer;
    private BoundedBuffer<String> connectionBuffer;


    public ControlWorker(String signature, Parents parents, BoundedBuffer<TCPPacket> controlBuffer, BoundedBuffer<String> connectionBuffer, OutBuffers outBuffers){
        this.signature = signature;
        this.parents = parents;
        this.outBuffers = outBuffers;
        this.controlBuffer = controlBuffer;
        this.connectionBuffer = connectionBuffer;
        this.grandParents = new HashSet<>();
        this.blackList = new HashSet<>();
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
                        System.out.println("ControlWorker send flood: " + this.signature + " -> " + neighbour);
                    }
                }
            }
        }

        System.out.println(this.parents);
        System.out.println("GRANDPARENTS: " + this.grandParents);
    }


    private void handleGrandFatherPacket(TCPGrandfatherControlPacket tcpGrandfatherPacket){
        if (tcpGrandfatherPacket.getProtocol() == GF_PROTOCOL.GRANDFATHER_REPLY){
            this.handleGrandFatherReply(tcpGrandfatherPacket);
        }
        else if (tcpGrandfatherPacket.getProtocol() == GF_PROTOCOL.GRANDFATHER_REQUEST){
            this.handleGrandFatherRequest(tcpGrandfatherPacket);
        }
    }


    private void handleConnectionStatePacket(TCPConnectionStatePacket tcpStatePacket){
        if (tcpStatePacket.getProtocol() == CS_PROTOCOL.CONNECTION_LOST){
            this.handleConnectionLost(tcpStatePacket);
        }
    }


    private void handleGrandFatherRequest(TCPGrandfatherControlPacket tcpGrandfatherPacket){

        String sender = tcpGrandfatherPacket.getSender();
        Set<String> parents = this.parents.getParents();

        // remover o sender, pode ser um dos meus pais
        parents.remove(sender);

        // criar um pacote com os meus pais e enviar ao filho
        TCPGrandfatherControlPacket reply = new TCPGrandfatherControlPacket(GF_PROTOCOL.GRANDFATHER_REPLY,parents);
        this.outBuffers.addPacket(sender,reply);

        System.out.println("ControlWorker receive grandfather request: " + this.signature + " <- " + sender);
        System.out.println("ControlWorker send grandfather reply: " + this.signature + " -> " + sender);
    }


    private void handleGrandFatherReply(TCPGrandfatherControlPacket tcpGrandfatherPacket){

        String sender = tcpGrandfatherPacket.getSender();
        this.grandParents = tcpGrandfatherPacket.getGrandparents();
        System.out.println("ControlWorker receive grandFather reply: " + this.signature + " <- " + sender);

        // se o nodo não tem pais, adicionar a lista negra
        if (this.grandParents.size() == 0){
            this.blackList.add(sender);
            System.out.println("ControlWorker add blacklist: " + sender);
        }
    }


    private void handleConnectionLost(TCPConnectionStatePacket tcpStatePacket){

        // informar o ConnectionWriteWorker que a ligacao terminou
        String neighbour = tcpStatePacket.getSender();
        this.outBuffers.addPacket(neighbour,tcpStatePacket);

        // remover o neighbour dos pais e eliminar o buffer
        this.parents.removeParent(neighbour);
        this.outBuffers.removeOutBuffer(neighbour);
        System.out.println("ControlWorker remove neighbour: " + neighbour);

        // se perdi a conexao com todos os pais, informar os filhos e contactar os avos
        if (this.parents.size() == 0){

            // os meus avos agora sao os meus pais
            // informar os meus filhos que o avo deles morreu
            TCPGrandfatherControlPacket info = new TCPGrandfatherControlPacket(
                GF_PROTOCOL.GRANDFATHER_REPLY, this.grandParents);

            for (String son : this.outBuffers.getKeys()){
                this.outBuffers.addPacket(son,info);
                System.out.println("ControlWorker send grandfather reply: " + this.signature + " -> " + son);
            }

            // entrar em contacto com os meus avos
            for (String grandParent : this.grandParents){
                try {this.connectionBuffer.push(grandParent);}
                catch (Exception e) {e.printStackTrace();}
            }

            // deixei de ter avos
            this.grandParents.clear();
        }
    }


    private void requestGrandParends(){

        TCPGrandfatherControlPacket requestGrandParents =
            new TCPGrandfatherControlPacket(GF_PROTOCOL.GRANDFATHER_REQUEST);

        // nunca faco um pedido nas primeiras iteracoes de flood
        if (this.history.size() > 1){

            // envio o pedido de tiver um pai e ainda nao conhecer os avos
            if (this.parents.size() == 1 && this.grandParents.size() == 0){    

                // enviar o pedido para todos pais (sera sempre um)
                for (String parent : this.parents.getParents()){

                    // o pai nao pode estar na lista negra
                    if (this.blackList.contains(parent) == false){
                        this.outBuffers.addPacket(parent,requestGrandParents);
                        System.out.println("ControlWorker send grandfather request: " + this.signature + " -> " + parent);
                    }
                }
            }
        }
    }


    public void run(){

        try{

            TCPPacket tcpPacket;

            while ((tcpPacket = this.controlBuffer.pop()) != null){

                // sempre que recebo um pacote verifico se nenhum dos pais é zombie
                this.parents.getZombies().forEach(x -> {this.parents.removeParent(x); System.out.println("AQUI");});

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
                        System.out.println("ControlWorker unknown tcpPacket:\n" + tcpPacket);
                        break;
                }

                // envia um pedido de grandfather caso seja necessario
                this.requestGrandParends();
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}