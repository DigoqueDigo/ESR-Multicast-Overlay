package node;
import java.util.Set;
import packet.tcp.TCPPacket;
import packet.tcp.TCPVideoControlPacket;
import struct.BoundedBuffer;
import struct.MapBoundedBuffer;
import struct.VideoConsumers;
import struct.VideoProviders;


public class NodeVideoControlWorker implements Runnable{

  //  private VideoProviders parents;
    private VideoConsumers videoTable;
    private BoundedBuffer<TCPPacket> videoBuffer;
    private MapBoundedBuffer<String,byte[]> videoStreams;
    private MapBoundedBuffer<String,TCPPacket> outBuffers;


    public NodeVideoControlWorker(VideoConsumers videoTable, VideoProviders parents, BoundedBuffer<TCPPacket> videoBuffer, MapBoundedBuffer<String,byte[]> videoStreams, MapBoundedBuffer<String,TCPPacket> outBuffers){
  //      this.parents = parents;
        this.videoTable = videoTable;
        this.videoBuffer = videoBuffer;
        this.videoStreams = videoStreams;
        this.outBuffers = outBuffers;
    }


    private void handleControlVideo(TCPVideoControlPacket videoControlPacket){
        switch (videoControlPacket.getProtocol()){
            case VIDEO_REQUEST:
                this.handleControlVideoRequest(videoControlPacket);
                break;
            case VIDEO_CANCEL:
                this.handleControlVideoCancel(videoControlPacket);
                break;
            case VIDEO_REPLY:
                this.handleControlVideoReply(videoControlPacket);
                break;
        }
    }


    private void handleControlVideoRequest(TCPVideoControlPacket videoControlPacket){

        String requester = videoControlPacket.getSender();
        String video = videoControlPacket.getVideo();

        // saber que o requester esta interessado no video
        this.videoTable.put(video,requester);

        // reencaminhar o pedido de video para o meu melhor pai
    //    String bestParent = this.parents.getBestParent();
    //    this.outBuffers.put(bestParent,videoControlPacket);
    }


    private void handleControlVideoCancel(TCPVideoControlPacket videoControlPacket){

        String video = videoControlPacket.getVideo();
        Set<String> requesters = this.videoTable.getNodes(video);

        for (String requester : requesters){

            // o cliente nao deseja continuar a receber o video
            if (this.videoStreams.containsKey(requester)){
                this.videoStreams.put(requester,new byte[0]);
            }
        }
    }


    private void handleControlVideoReply(TCPVideoControlPacket videoControlPacket){

        String video = videoControlPacket.getVideo();
        Set<String> requesters = this.videoTable.getNodes(video);

        // iterar sobre quem esta interessado no video
        for (String requester : requesters){

            // o requester pode ser um cliente
            if (this.videoStreams.containsKey(requester)){
                this.videoStreams.put(requester,videoControlPacket.getData());
            }

            // se o requester nao e uma cliente, entao faz parte do overlay
            else{
                this.outBuffers.put(requester,videoControlPacket);
            }
        }    
    }


    public void run(){

        try{

            TCPPacket tcpPacket;

            while ((tcpPacket = videoBuffer.pop()) != null){

                switch (tcpPacket.getType()){

                    case CONTROL_VIDEO:
                        this.handleControlVideo((TCPVideoControlPacket) tcpPacket);
                        break;

                    default:
                        System.out.println("StreamControl unknown tcpPacket:\n" + tcpPacket);
                        break;
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}