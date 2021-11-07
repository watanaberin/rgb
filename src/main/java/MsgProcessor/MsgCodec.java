package MsgProcessor;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteIterator;
import akka.util.ByteString;
import akka.util.ByteStringBuilder;
import com.google.gson.Gson;
import entity.Message;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class MsgCodec extends UntypedAbstractActor {
    private static final Gson gson = new Gson();

    private final ActorRef connection;
    private final ActorRef msgHandler;
    private ByteString buf = ByteString.emptyByteString();

    public MsgCodec(ActorRef connection, ActorRef msgHandler) {
        this.connection = connection;
        this.msgHandler = msgHandler;
    }

    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Tcp.Received) {
            final ByteString data = ((Tcp.Received) msg).data();
            buf = buf.concat(data);
            decodeMsg();
        } else if (msg instanceof Tcp.ConnectionClosed) {
            getContext().stop(getSelf());
        } else if (msg instanceof Message) {
            final ByteString data = encodeMsg(msg);
            connection.tell(TcpMessage.write(data), getSelf());
        }
    }
    private void decodeMsg() {
        while (buf.length() > 8) {
            final ByteIterator it = buf.iterator();
            final int msgId = it.getInt(ByteOrder.BIG_ENDIAN);
            final int jsonLength =it.getInt(ByteOrder.BIG_ENDIAN);

            if (buf.length() >= 8 + jsonLength) {
                final Object msg= decodeMsg(msgId, buf.slice(8, 8+jsonLength));
                buf = buf.drop(8 + jsonLength);
                msgHandler.tell(msg, getSelf());
            }
        }
    }
    private Object decodeMsg(int msgId, ByteString jsonData) {
        final Class<?> msgClass = MsgRegister.getMsgClass(msgId);
        final Reader reader = new InputStreamReader(
                jsonData.iterator().asInputStream(),
                StandardCharsets.UTF_8);

        return gson.fromJson(reader, msgClass);
    }

    private ByteString encodeMsg(Object msg) {
        final int msgId = MsgRegister.getMsgId(msg);
        final byte[] jsonBytes = gson.toJson(msg)
                .getBytes(StandardCharsets.UTF_8);

        final ByteStringBuilder bsb = new ByteStringBuilder();
        bsb.putInt(msgId, ByteOrder.BIG_ENDIAN);
        bsb.putInt(jsonBytes.length, ByteOrder.BIG_ENDIAN);
        bsb.putBytes(jsonBytes);

        return bsb.result();
    }
}
