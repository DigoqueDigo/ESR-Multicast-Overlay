import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;


public class UDPStreamServer {

    public static void main(String[] args) throws InterruptedException{

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        MediaPlayer mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();

        StringBuilder buffer = new StringBuilder();

        buffer.append(":sout=#rtp{dst=").append(args[0]);
        buffer.append(",port=").append(args[1]);
        buffer.append(",mux=ts}");

        String file = args[2];
        String options = buffer.toString();

        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                System.out.println("Playing video");
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                System.out.println("Finished");
            }
        });

        mediaPlayer.media().play(file, options);
        Thread.currentThread().join();
    }
}