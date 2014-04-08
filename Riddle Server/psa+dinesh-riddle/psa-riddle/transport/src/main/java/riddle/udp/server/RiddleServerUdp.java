package riddle.udp.server;

import riddle.sink.MsgSink;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: prabath
 */
public class RiddleServerUdp {

    private int port = 5555;

    private DatagramChannel srvChannel;

    private DatagramSocket srvSocket;

    private AtomicBoolean isStopSrv;

    private ExecutorService connectionExecutor;

    private Map<String, DatagramHandler> datagramHandlerMap;

    private MsgSink msgSink;

    public void initServer() throws IOException {
        System.out.println("Starting Riddle server on port " + port);
        datagramHandlerMap = new HashMap<>();
        connectionExecutor = Executors.newFixedThreadPool(10);
        isStopSrv = new AtomicBoolean(false);
        srvChannel = DatagramChannel.open();
        srvSocket = srvChannel.socket();
        srvSocket.bind(new InetSocketAddress(port));
        srvChannel.configureBlocking(false);
        System.out.println("Successfully started Riddle server");
        startListning();
    }

    private void startListning() {
        while (!isStopSrv.get()) {
            try {
                final ByteBuffer inBuff = ByteBuffer.allocate(50);
                SocketAddress remoteAddress = srvChannel.receive(inBuff);
                if (remoteAddress != null) {
                    String key = remoteAddress.toString();
                    if (!datagramHandlerMap.containsKey(key)) {
                        DatagramHandler datagramHandler = new DatagramHandler(remoteAddress);
                        datagramHandlerMap.put(key, datagramHandler);
                        datagramHandler.sendUpStream(inBuff);
                    }else {
                       datagramHandlerMap.get(key).sendUpStream(inBuff);
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }

        }
    }

    private class DatagramHandler {

        private SocketAddress address;

        private DatagramHandler(SocketAddress address) {
            this.address = address;
        }

        public void sendUpStream(final ByteBuffer inBuff) {
            connectionExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        inBuff.flip();
                        ByteBuffer reply = msgSink.sinkMsg(inBuff);
                        sendReply(reply);
                    } catch (Throwable th) {
                       th.printStackTrace();
                    }
                }
            });
        }

        private void sendReply(ByteBuffer replyBuf) throws IOException {
            if (replyBuf != null) {
                srvChannel.send(replyBuf, address);
            }
        }
    }

    private String getMsg(ByteBuffer buffer) {
        buffer = buffer.slice();
        byte[] msgArr = new byte[buffer.limit()];
        buffer.get(msgArr, 0, buffer.limit());
        String msg = new String(msgArr);
        return msg;
    }

    public void setMsgSink(MsgSink msgSink) {
        this.msgSink = msgSink;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
