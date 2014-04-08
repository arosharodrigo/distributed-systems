package riddle.udp.client;

import riddle.sink.ConsoleMsgSink;
import riddle.source.ConsoleMsgSource;
import riddle.util.ClientReplyProcessor;

/**
 * @author: prabath
 */
public class ClientSimulatorUdp {

    public static void main(String[] args) {
        RiddleClientUdp riddleClient = new RiddleClientUdp();
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
