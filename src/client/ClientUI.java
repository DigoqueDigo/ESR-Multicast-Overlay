package client;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
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
import node.stream.NodeStreamWorker;
import struct.EdgeProviders;


public class ClientUI{

    private Terminal terminal;
    private EdgeProviders edgeProviders;


    public ClientUI(EdgeProviders edgeProviders) throws IOException{
        this.edgeProviders = edgeProviders;
        this.terminal = TerminalBuilder.builder().dumb(true).build();
    }


    public void close() throws IOException{
        this.terminal.close();
    }


    private String select_option(Supplier<String> prompt, Supplier<List<String>> options) throws EndOfFileException{

        String userChoice = null;

        while (userChoice == null){

            StringsCompleter stringsCompleter = new StringsCompleter(options.get());

            LineReader reader = LineReaderBuilder.builder()
                .terminal(this.terminal)
                .completer(stringsCompleter).build();

            userChoice = reader.readLine(prompt.get());
            userChoice = userChoice.strip();

            if (options.get().contains(userChoice) == false){
                userChoice = null;
            }
        }

        return userChoice;
    }


    public void start(){

        String video_prompt = new AttributedString("Select Video >> ",
            AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.WHITE)).toAnsi();

        String edge_prompt = new AttributedString("Select EdgeNode >> ",
            AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.WHITE)).toAnsi();

        String bye = new AttributedString("Bye!",
            AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.WHITE)).toAnsi();

        try{

            while (true){

                List<String> videoList = null;
                String selectedEdgeNode = null;

                while (videoList == null){

                    selectedEdgeNode = this.select_option(
                        () -> this.edgeProviders.toString() + "\n" + edge_prompt,
                        () -> this.edgeProviders.rowKeySet().stream().collect(Collectors.toList()));

                    ClientVideoGather clientVideoGrather = new ClientVideoGather(
                        selectedEdgeNode,NodeStreamWaitClient.CLIENT_ESTABLISH_CONNECTION_PORT);

                    videoList = clientVideoGrather.getVideoList();
                }

                final List<String> finalVideoList = videoList;
                String selectedVideo = this.select_option(
                    () -> video_prompt,
                    () -> finalVideoList);

                ClientStream clientConnection = new ClientStream(
                    selectedVideo,
                    selectedEdgeNode,
                    NodeStreamWaitClient.CLIENT_ESTABLISH_CONNECTION_PORT,
                    NodeStreamWorker.STREAMING_PORT
                );

                clientConnection.start();
            }
        }

        catch (EndOfFileException e){
            System.out.println(bye);
        }
    }
}