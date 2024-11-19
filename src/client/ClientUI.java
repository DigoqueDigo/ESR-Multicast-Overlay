package client;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import node.stream.NodeStreamWaitClient;


public class ClientUI{

    private List<String> edgeNodes;
    private Terminal terminal;


    public ClientUI(List<String> edgeNodes) throws IOException{
        this.edgeNodes = edgeNodes.stream().collect(Collectors.toList());
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

                List<String> videoList = null;
                String selectedEdgeNode = null;

                while (videoList == null){

                    selectedEdgeNode = this.select_option(edge_prompt,warning,this.edgeNodes);
                    ClientVideoGather clientVideoGrather = new ClientVideoGather(
                        selectedEdgeNode,NodeStreamWaitClient.CLIENT_ESTABLISH_CONNECTION_PORT);

                    videoList = clientVideoGrather.getVideoList();
                }

                String selectedVideo = this.select_option(video_prompt, warning, videoList);
                ClientConnection clientConnection = new ClientConnection(selectedEdgeNode,selectedVideo);
                clientConnection.start();
            }
        }

        catch (EndOfFileException e){
            System.out.println(bye);
        }
    }
}