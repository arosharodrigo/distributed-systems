package riddle.udp.server;

import riddle.sink.ConsoleMsgSink;
import riddle.util.ServerReplyProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: prabath
 */
public class ServerSimulatorUdp {

    public static void main(String[] args) {
        final RiddleServerUdp riddleServer = new RiddleServerUdp();
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
