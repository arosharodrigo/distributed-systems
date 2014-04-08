package riddle.tcp.server;

import riddle.sink.MsgSink;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: prabath
 */
public class RiddleServer {

    private int port = 5555;

    private ServerSocketChannel srvSocketChannel;

    private ServerSocket srvSocket;

    private AtomicBoolean isStopSrv;

    private ExecutorService connectionExecutor;

    private MsgSink msgSink;

    public void initServer() throws IOException {
        System.out.println("Starting Riddle server on port " + port);
        connectionExecutor = Executors.newFixedThreadPool(10);
        isStopSrv = new AtomicBoolean(false);
        srvSocketChannel = ServerSocketChannel.open();
        srvSocket = srvSocketChannel.socket();
        srvSocket.bind(new InetSocketAddress(port));
        srvSocketChannel.configureBlocking(false);
        System.out.println("Successfully started Riddle server");
        startListning();
    }

    private void startListning() {
        while (!isStopSrv.get()) {
            try {
                SocketChannel sc = srvSocketChannel.accept();
                if (sc != null) {
                    sc.configureBlocking(false);
                    connectionExecutor.submit(new SocketChannelHandler(sc));
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }

        }
    }

    private class SocketChannelHandler implements Runnable {

        private SocketChannel sc;

        private String SEND_BK = "test_from_server";

        private long id;

        private AtomicBoolean isStop;

        private SocketChannelHandler(SocketChannel sc) {
            this.sc = sc;
            this.id = System.nanoTime();
            isStop = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            try {
                while (!isStop.get()) {
                    ByteBuffer inBuff = ByteBuffer.allocate(50);
                    inBuff.putLong(id);
                    int count = sc.read(inBuff);
                    if (count > 0) {
                        inBuff.flip();
                        ByteBuffer reply = msgSink.sinkMsg(inBuff);
                        sendReply(reply);
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        private void sendReply(ByteBuffer replyBuf) throws IOException {
            if (replyBuf != null) {
                String reply = getMsg(replyBuf);
                if (reply.equals("bye")) {
                    try {
                        isStop.set(true);
                    } finally {
                        scheduleSocketClose();
                    }
                }
                sc.write(replyBuf);
            }
        }

        private Future<?> scheduleSocketClose() {
            return connectionExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        sc.socket().close();
                        sc.close();
                    } catch (Throwable th) {

                    }
                }
            });
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
