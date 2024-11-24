package node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import packet.tcp.TCPConnectionStatePacket;
import packet.tcp.TCPFloodControlPacket;
import packet.tcp.TCPGrandfatherControlPacket;
import packet.tcp.TCPPacket;
import packet.tcp.TCPPacket.TCP_TYPE;
import packet.tcp.TCPGrandfatherControlPacket.GRANDFATHER_PROTOCOL;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;
import struct.VideoProviders;
import packet.tcp.TCPConnectionStatePacket.CONNECTION_STATE_PROTOCOL;


public class NodeFloodControlWorker implements Runnable{

    private static final int HISTORY_SIZE = 100;

    private final String signature;
    private VideoProviders videoProviders;

    private Queue<String> history;
    private Map<String,Set<String>> blackList;
    private Map<String,Set<String>> grandParentsVideoProviders;

    private BoundedBuffer<String> connectionBuffer;
    private BoundedBuffer<TCPPacket> controlBuffer;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public NodeFloodControlWorker(String signature, VideoProviders videoProviders, BoundedBuffer<TCPPacket> controlBuffer, BoundedBuffer<String> connectionBuffer, MapBoundedBuffer<String,TCPPacket> outBuffers){
        this.signature = signature;
        this.videoProviders = videoProviders;
        this.controlBuffer = controlBuffer;
        this.connectionBuffer = connectionBuffer;
        this.outBuffers = outBuffers;
        this.blackList = new HashMap<>();
        this.grandParentsVideoProviders = new HashMap<>();
        this.history = new CircularFifoQueue<>(HISTORY_SIZE);
    }


    private void handleControlFlood(TCPFloodControlPacket tcpFloodPacket){

        // inspeciono o pacote se nao tiver a minha assinatura
        if (tcpFloodPacket.getSignatures().contains(signature) == false){

            String sender = tcpFloodPacket.getSenderIP();
            List<String> videos = tcpFloodPacket.getVideos();

            Long serverTimeStamp = tcpFloodPacket.getTimestamp();
            long delay = System.currentTimeMillis() - serverTimeStamp;

            // adiciono o nodo como provider se for o primeiro pacote que recebo dele
            String identifier = sender + serverTimeStamp.toString();

            if (this.history.contains(identifier) == false){
                this.history.add(identifier);
                videos.stream().forEach(video -> {
                    this.videoProviders.addProvider(video,sender,delay);    
                    this.blackList.putIfAbsent(video,new HashSet<>());
                    this.grandParentsVideoProviders.putIfAbsent(video,new HashSet<>());
                });
            }

            // adicionar a minha assinatura ao pacote de control flood
            tcpFloodPacket.addSignature(signature);

            // enviar o pacote para todas as interfaces execeto a do sender
            for (String neighbour : this.outBuffers.getKeys()){
                if (neighbour.equals(sender) == false){
                    this.outBuffers.put(neighbour,tcpFloodPacket);
                    System.out.println("NodeFloodControlWorker send flood: " + this.signature + " -> " + neighbour);
                }
            }
        }

        System.out.println(this.videoProviders);
        System.out.println("GRANDPARENTS: " + this.grandParentsVideoProviders);
        System.out.println("BLACKLIST: " + this.blackList);
    }

    private void handleGrandFatherRequest(TCPGrandfatherControlPacket tcpGrandfatherPacket){

        String sender = tcpGrandfatherPacket.getSenderIP();
        String video = tcpGrandfatherPacket.getVideo();
        Set<String> providers = this.videoProviders.getProviders(video);

        // remover o sender, pode ser um dos meus providers
        providers.remove(sender);

        // criar um pacote com os meus providers e enviar ao filho
        TCPGrandfatherControlPacket reply = new TCPGrandfatherControlPacket(
            GRANDFATHER_PROTOCOL.GRANDFATHER_REPLY,video,providers.stream().collect(Collectors.toList()));

        this.outBuffers.put(sender,reply);

        System.out.println("NodeFloodControlWorker (grandfather request) receive grandfather request: " + this.signature + " <- " + sender);
        System.out.println("NodeFloodControlWorker (grandfather request) send grandfather reply: " + this.signature + " -> " + sender);
    }


    private void handleGrandFatherReply(TCPGrandfatherControlPacket tcpGrandfatherPacket){

        String sender = tcpGrandfatherPacket.getSenderIP();
        String video = tcpGrandfatherPacket.getVideo();
        Set<String> providers = tcpGrandfatherPacket.getGrandparents().stream().collect(Collectors.toSet());

        // atualizar os avos do video
        this.grandParentsVideoProviders.put(video,providers);
        System.out.println("NodeFloodControlWorker (grandfather reply) receive grandFather reply: " + this.signature + " <- " + sender);
    }


    private void handleConnectionLost(TCPConnectionStatePacket tcpStatePacket){

        // informar o ConnectionWriteWorker que a ligacao terminou
        String neighbour = tcpStatePacket.getSenderIP();
        this.outBuffers.put(neighbour,tcpStatePacket);

        // remover o neighbour dos pais e eliminar o buffer
        this.videoProviders.removeProvider(neighbour);
        this.outBuffers.removeBoundedBuffer(neighbour);

        System.out.println("NodeFloodControlWorker (connection lost) remove neighbour: " + neighbour);

        // para cada video tenho de verificar se fiquei sem providers
        for (String video : this.videoProviders.getVideos()){

            if (this.videoProviders.getProviders(video).size() == 0){

                // informar os meus filhos que o avo deles morreu
                Set<String> grandProviders = this.grandParentsVideoProviders.get(video);

                TCPGrandfatherControlPacket info = new TCPGrandfatherControlPacket(
                    GRANDFATHER_PROTOCOL.GRANDFATHER_REPLY, video, grandProviders.stream().collect(Collectors.toList()));

                for (String son : this.outBuffers.getKeys()){
                    this.outBuffers.put(son,info);
                    System.out.println("NodeFloodControlWorker (connection lost) send grandfather reply: " + this.signature + " -> " + son + " (" + video + ")");
                }

                // entrar em contacto com os meus avos
                for (String grandParent : this.grandParentsVideoProviders.get(video)){
                    try {this.connectionBuffer.push(grandParent);}
                    catch (Exception e) {e.printStackTrace();}
                }
            }

            // o algoritmo funciona melhor se esquecer os avos
            this.grandParentsVideoProviders.get(video).clear();
            this.blackList.get(video).clear();
        }
    }


    private void requestGrandParends(){

        // nunca faco um pedido nas primeiras iteracoes de flood
        if (this.history.size() > 1){

            // tenho de verificar os providers de cada video
            for (String video : this.videoProviders.getVideos()){

                // envio o pedido de tiver um pai e ainda nao conhecer os avos
                if (this.videoProviders.getProviders(video).size() == 1 && this.grandParentsVideoProviders.get(video).size() == 0){    

                    // enviar o pedido para o provider que me resta
                    for (String provider : this.videoProviders.getProviders(video)){

                        // o provider nao pode estar na lista negra
                        if (this.blackList.get(video).contains(provider) == false){

                            TCPGrandfatherControlPacket requestGrandParent =
                                new TCPGrandfatherControlPacket(GRANDFATHER_PROTOCOL.GRANDFATHER_REQUEST,video);

                            this.blackList.get(video).add(provider);
                            System.out.println("NodeFloodControlWorker (trigger) add blacklist: " + video + " :: " + provider);

                            this.outBuffers.put(provider,requestGrandParent);
                            System.out.println("NodeFloodControlWorker (trigger) send grandfather request: " + this.signature + " -> " + provider);
                            System.out.println(requestGrandParent);
                        }
                    }
                }
            }
        }
    }


    public void run(){

        TCPPacket tcpPacket;
        Map<TCP_TYPE,Consumer<TCPPacket>> handlers = new HashMap<>();
        Map<GRANDFATHER_PROTOCOL,Consumer<TCPGrandfatherControlPacket>> grandFatherHandlers = new HashMap<>();
        Map<CONNECTION_STATE_PROTOCOL,Consumer<TCPConnectionStatePacket>> connectionStateHandlers = new HashMap<>();

        grandFatherHandlers.put(GRANDFATHER_PROTOCOL.GRANDFATHER_REPLY, packet -> this.handleGrandFatherReply(packet));
        grandFatherHandlers.put(GRANDFATHER_PROTOCOL.GRANDFATHER_REQUEST, packet -> this.handleGrandFatherRequest(packet));
        connectionStateHandlers.put(CONNECTION_STATE_PROTOCOL.CONNECTION_LOST, packet -> this.handleConnectionLost(packet));

        handlers.put(TCP_TYPE.CONTROL_FLOOD, packet -> this.handleControlFlood((TCPFloodControlPacket)packet));

        handlers.put(TCP_TYPE.CONTROL_GRANDFATHER, packet -> {
            TCPGrandfatherControlPacket grandfatherControlPacket = (TCPGrandfatherControlPacket) packet;
            grandFatherHandlers.get(grandfatherControlPacket.getProtocol()).accept(grandfatherControlPacket);
        });

        handlers.put(TCP_TYPE.CONTROL_CONNECTION_STATE, packet -> {
            TCPConnectionStatePacket connectionStatePacket = (TCPConnectionStatePacket) packet;
            connectionStateHandlers.get(connectionStatePacket.getProtocol()).accept(connectionStatePacket);
        });

        while ((tcpPacket = this.controlBuffer.pop()) != null){

            if (handlers.containsKey(tcpPacket.getType())){
                handlers.get(tcpPacket.getType()).accept(tcpPacket);
            }

            else System.out.println("NodeFloodControlWorker unknown packet: " + tcpPacket);

            // envia um pedido de grandfather caso seja necessario
            this.requestGrandParends();
        }
    }
}