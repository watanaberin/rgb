package actor;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import akka.io.TcpMessage;

import java.net.InetSocketAddress;

public class Accepter extends UntypedAbstractActor {
    private final ActorRef tcpManager;

    public Accepter(ActorRef tcpManager) {
        this.tcpManager = tcpManager;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        System.out.println("Accepter received:" + message);
        if (message instanceof Integer) {
            final int port = (Integer) message;
            final InetSocketAddress endpoint = new InetSocketAddress("localhost",port);
            final Object cmd = TcpMessage.bind(getSelf(), endpoint, 100);
            tcpManager.tell(cmd, getSelf());
        }
    }
}
