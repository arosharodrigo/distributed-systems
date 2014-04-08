package riddle.util;

import riddle.service.ChannelChangeListener;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author: prabath
 */
public interface ReplyProcessor {

    void registerListerner(ChannelChangeListener listerner);

    ByteBuffer createReply(ByteBuffer buffer) throws IOException;
}
