package client;
import javax.swing.JFrame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import node.stream.NodeStreamVlcjWorker;


public class ClientPlayer implements Runnable{

    private boolean wasReleased;
    private final Object lock;
    private final JFrame frame;
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;


    public ClientPlayer(String windowTitle){
        this.wasReleased = false;
        this.lock = new Object();
        this.frame = new JFrame(windowTitle);
        this.mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    }


    private void handleWindowClosing(){
        synchronized (this.lock){
            this.frame.setVisible(false);
            this.mediaPlayerComponent.release(); 
            this.frame.dispose();
            this.wasReleased = true;
            this.lock.notify();
        }
    }


    private void handleVideoPlaying(){
        System.out.println("Playing video");
    }


    private void handleVideoError(){
        System.err.println("Video error");
    }


    public void run(){

        try{

            this.frame.setLocation(100,100);
            this.frame.setSize(600,400);
            this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.frame.setContentPane(this.mediaPlayerComponent);

            this.frame.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e) {
                    handleWindowClosing();
                }
            });

            this.mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter(){

                public void playing(MediaPlayer mediaPlayer) {
                    handleVideoPlaying();
                }

                public void error(MediaPlayer mediaPlayer) {
                    handleVideoError();
                }
            });

            this.frame.setVisible(true);
            this.mediaPlayerComponent.mediaPlayer().media().play("rtp://0.0.0.0:" + NodeStreamVlcjWorker.STREAMING_PORT);

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