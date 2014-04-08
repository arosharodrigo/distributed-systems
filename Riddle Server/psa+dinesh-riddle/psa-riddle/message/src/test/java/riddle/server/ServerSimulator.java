package riddle.server;

import riddle.sink.ConsoleMsgSink;
import riddle.tcp.server.RiddleServer;
import riddle.util.ServerReplyProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: prabath
 */
public class ServerSimulator {

    public static void main(String[] args) {
        final RiddleServer riddleServer = new RiddleServer();
        ConsoleMsgSink msgSink = new ConsoleMsgSink();
        msgSink.setReplyProcessor(new ServerReplyProcessor());
        msgSink.setTag("MSG SERVER : ");
        riddleServer.setMsgSink(msgSink);
        riddleServer.setPort(5555);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        riddleServer.initServer();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
            Thread.sleep(1000);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

}
