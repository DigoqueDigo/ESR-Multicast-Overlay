package node.stream;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;


public class NodeStreamVlcjWorker implements Runnable{

    public static final int STREAMING_PORT = 7000;

    private String fifo;
    private String clientIP;
    private boolean wasReleased;
    private final Object lock;
    private final MediaPlayer mediaPlayer;
    private final MediaPlayerFactory mediaPlayerFactory;


    public NodeStreamVlcjWorker(String clientIP, String fifo){
        this.fifo = fifo;
        this.clientIP = clientIP;
        this.wasReleased = false;
        this.lock = new Object();
        this.mediaPlayerFactory = new MediaPlayerFactory();
        this.mediaPlayer = this.mediaPlayerFactory.mediaPlayers().newMediaPlayer();
    }


    private void handleVideoPlaying(){
        System.out.println("Playing video");
    }


    private void handleVideoFinished(){

        synchronized (this.lock){

            try{
                System.out.println("NodeStreamVlcjWorker trying to free up resources");    

                this.mediaPlayer.release();
                System.out.println("NodeStreamVlcjWorker MediaPlayer released");

                this.mediaPlayerFactory.release();
                System.out.println("NodeStreamVlcjWorker MediaPlayerFactory released");
            }

            catch (Exception e){
                e.printStackTrace();
            }

            finally{
                this.wasReleased = true;
                this.lock.notify();
                System.out.println("NodeStreamVlcjWorker all resources have been released");
            }
        }
    }


    private String formatRtpStream(String ip, int port){
        StringBuilder buffer = new StringBuilder();
        buffer.append(":sout=#rtp{dst=").append(ip);
        buffer.append(",port=").append(port);
        buffer.append(",mux=ts}");
        return buffer.toString();
    }


    public void run(){

        try{

            this.mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

                public void playing(MediaPlayer mediaPlayer) {
                    mediaPlayer.submit(new Runnable() {
                        public void run(){
                            handleVideoPlaying();
                        }
                    });
                }

                public void finished(MediaPlayer mediaPlayer) {
                    mediaPlayer.submit(new Runnable() {
                        public void run(){
                            handleVideoFinished();
                        }
                    });
                }

                public void error(MediaPlayer mediaPlayer) {
                    mediaPlayer.submit(new Runnable() {
                        public void run(){
                            handleVideoFinished();
                        }
                    });
                }
            });

            this.mediaPlayer.media().play(this.fifo, this.formatRtpStream(this.clientIP, STREAMING_PORT));

            while (this.wasReleased == false){
                synchronized (this.lock){
                    this.lock.wait();
                }
            }
        }

        catch (InterruptedException e){
            e.printStackTrace();
        }
    }   
}