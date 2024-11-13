package client;
import javax.swing.JFrame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;


public class ClientPlayer implements Runnable{

    public static final int CLIENT_STREAMING_PORT = 7000;

    private final Object lock;
    private final JFrame frame;
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;


    public ClientPlayer(String windowTitle){
        this.lock = new Object();
        this.frame = new JFrame(windowTitle);
        this.mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    }


    private void handleWindowClosing(){
        synchronized (lock){
            frame.setVisible(false);
            mediaPlayerComponent.release(); 
            frame.dispose();
            lock.notify();
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

            this.mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

                public void playing(MediaPlayer mediaPlayer) {
                    handleVideoPlaying();
                }

                public void error(MediaPlayer mediaPlayer) {
                    handleVideoError();
                }
            });

            this.frame.setVisible(true);
            this.mediaPlayerComponent.mediaPlayer().media().play("rtp://0.0.0.0:" +  CLIENT_STREAMING_PORT);

            while (frame.isVisible()){
                synchronized (lock){
                    lock.wait();
                }
            }
        }

        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}