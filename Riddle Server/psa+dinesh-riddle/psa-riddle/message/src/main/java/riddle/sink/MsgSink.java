package riddle.sink;

import java.nio.ByteBuffer;

/**
 * @author: prabath
 */
public interface MsgSink {

    ByteBuffer sinkMsg(ByteBuffer inBuff);
}
