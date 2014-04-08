package riddle.source;

import riddle.service.ChannelChangeListener;
import riddle.sink.MsgSink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: prabath
 */
public class ConsoleMsgSource implements MSgSource, ChannelChangeListener {

    private MsgSink msgSink;

    private AtomicBoolean isStop = new AtomicBoolean(false);

    @Override
    public void readMsg() {
        while (!isStop.get()) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.print("COMMAND : ");
                String line = consoleReader.readLine();
                System.out.println(line);
                msgSink.sinkMsg(ByteBuffer.wrap(line.getBytes()));
            } catch (Exception e) {
                System.err.println("Error in reading from console. " + e);
            }
        }
        System.out.println("Client Stopped");
    }

    @Override
    public void notifyChannelClose() {
        isStop.set(true);
        System.exit(0);
    }

    public void setMsgSink(MsgSink msgSink) {
        this.msgSink = msgSink;
    }
}
