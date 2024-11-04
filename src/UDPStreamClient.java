import javax.swing.JFrame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;


public class UDPStreamClient {

    public static void main(String[] args){

        JFrame frame = new JFrame("ESR - Multicast Overlay");
        EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        frame.setLocation(100,100);
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                mediaPlayerComponent.release(); 
                System.exit(0);
            }
        });

        frame.setContentPane(mediaPlayerComponent);
        frame.setVisible(true);
        mediaPlayerComponent.mediaPlayer().media().play("rtp://" + args[0] + ":" +  args[1]);
    }
}