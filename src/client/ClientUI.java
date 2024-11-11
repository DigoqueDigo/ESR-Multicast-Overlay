package client;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;


public class ClientUI{

    private List<String> edgeNodes;
    private List<String> videos;
    private Terminal terminal;


    public ClientUI(List<String> edgeNodes, List<String> videos) throws IOException{
        this.edgeNodes = new ArrayList<>(edgeNodes);
        this.videos = new ArrayList<>(videos);
        this.terminal = TerminalBuilder.builder().dumb(true).build();
    }


    public void close() throws IOException{
        this.terminal.close();
    }


    private String select_option(String prompt, String warning, List<String> options) throws EndOfFileException{

        String userChoice = null;
        StringsCompleter stringsCompleter = new StringsCompleter(options);

        LineReader reader = LineReaderBuilder.builder()
            .terminal(this.terminal)
            .completer(stringsCompleter).build();

        while (userChoice == null){

            userChoice = reader.readLine(prompt);
            userChoice = userChoice.strip();

            if (options.contains(userChoice) == false){
                userChoice = null;
                System.out.println(warning);
            }
        }

        return userChoice;
    }


    public void start(){

        String video_prompt = new AttributedString("Select one video >> ",
            AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.WHITE)).toAnsi();

        String edge_prompt = new AttributedString("Select one EdgeNode >> ",
            AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.WHITE)).toAnsi();

        String warning = new AttributedString("Invalid option selected!",
            AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.RED)).toAnsi();

        String bye = new AttributedString("Bye!",
            AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.WHITE)).toAnsi();

        try{

            while (true){

                String selectedVideo = this.select_option(video_prompt, warning, this.videos);
                String selectedEdgeNode = this.select_option(edge_prompt, warning, this.edgeNodes);

                ClientPlayer clientPlayer = new ClientPlayer(selectedEdgeNode,selectedVideo);
                clientPlayer.play();
            }
        }

        catch (EndOfFileException e){
            System.out.println(bye);
        }
    }
}