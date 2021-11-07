package org.example;

import akka.actor.ActorSystem;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ActorSystem system = ActorSystem.create("ActorSystem");
    }
}
