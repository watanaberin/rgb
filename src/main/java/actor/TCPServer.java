package actor;

import MsgProcessor.MsgCodec;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.io.Tcp;
import akka.io.TcpMessage;

import java.net.InetSocketAddress;

public class TCPServer extends UntypedAbstractActor {

    private final ActorRef msgHandler;

    public TCPServer(ActorRef msgHandler) {
        this.msgHandler = msgHandler;
    }

    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Integer) {
            final int port = (Integer) msg;
            startServer(port);
        } else if (msg instanceof Tcp.Bound) {
            getSender().tell(msg, getSelf());
        } else if (msg instanceof Tcp.CommandFailed) {
            getSender().tell(msg, getSelf());
        } else if (msg instanceof Tcp.Connected) {
            final Tcp.Connected conn = (Tcp.Connected) msg;
            getSender().tell(conn, getSelf());

        }
    }

    private void startServer(int port) {
        final InetSocketAddress endPoint = new InetSocketAddress("localhost", port);
        final Object cmd = TcpMessage.bind(getSelf(), endPoint, 100);
        Tcp.get(getContext().system()).getManager().tell(cmd, getSelf());
    }

    private void registerCodec(ActorRef connection){
        final Props codecProps =Props.create(MsgCodec.class, connection);
        final ActorRef codec = getContext().actorOf(codecProps);
        connection.tell(TcpMessage.register(codec),getSelf());
    }
}
