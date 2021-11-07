package actor;

import akka.actor.UntypedAbstractActor;

public class MyActor extends UntypedAbstractActor {
    private String recive;

    public MyActor(String recive) {
        this.recive = recive;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        System.out.println(super.context());
        System.out.println(recive);
        if (message instanceof String) {
            String msg = (String) message;
            System.out.println(msg);
        }
        System.out.println("Hello world");
    }
}