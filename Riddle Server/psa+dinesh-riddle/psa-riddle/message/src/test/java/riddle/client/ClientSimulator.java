package riddle.client;

import riddle.sink.ConsoleMsgSink;
import riddle.source.ConsoleMsgSource;
import riddle.tcp.client.RiddleClient;
import riddle.util.ClientReplyProcessor;

/**
 * @author: prabath
 */
public class ClientSimulator {

    public static void main(String[] args) {
        RiddleClient riddleClient = new RiddleClient();
        ConsoleMsgSink msgSink = new ConsoleMsgSink();
        ClientReplyProcessor replyProcessor = new ClientReplyProcessor();
        msgSink.setReplyProcessor(replyProcessor);
        msgSink.setTag("MSG CLIENT : ");
        riddleClient.setMsgSink(msgSink);

        ConsoleMsgSource msgSource = new ConsoleMsgSource();
        msgSource.setMsgSink(riddleClient);
        replyProcessor.registerListerner(msgSource);

        riddleClient.startClient();
        msgSource.readMsg();

    }
}
