package no.ntnu.imt3281.ludo.server;

import no.ntnu.imt3281.ludo.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ServerTest {

    Server server;
    Client client1;
    Client client2;
    Client client3;
    Client client4;

    @Before
    public void setUp() throws Exception {
        this.server = new Server();
        this.client1 = new Client();
        this.client2 = new Client();
        this.client3 = new Client();
        this.client4 = new Client();
        client1.connect("REGISTER:", "Johan", "hei");
        client2.connect("REGISTER:", "Brede", "HEI");
        client3.connect("REGISTER:", "Marius", "hei");
        client4.connect("REGISTER:", "Okolloen", "hei");
        try{
            sleep(600); //wait 600ms to let the system connect
        }catch (InterruptedException e){

        }
    }

    @After
    public void tearDown() throws Exception {
        this.server.killServer();
    }

    @Test
    public void testGameSetup(){

        client1.requestNewGame();
        client2.requestNewGame();
        client3.requestNewGame();
        client4.requestNewGame();
        //server.ludoServer.newGame("TEST");
        //server.ludoServer.addPlayerToGame("TEST", client1.getName());
        //server.ludoServer.addPlayerToGame("TEST", client2.getName());
        try{
            sleep(1500); //wait 1s to let the request run through server
        }catch (InterruptedException e){

        }

        //everyone chucks a dice event, so it has to go through
        client1.sendDiceEvent(client1.test2); //send event
        client2.sendDiceEvent(client2.test2); //send event
        client3.sendDiceEvent(client3.test2); //send event
        client4.sendDiceEvent(client4.test2); //send event

        try{
            sleep(2000); //wait 2s to let the message run through
        }catch (InterruptedException e){

        }

        assertTrue(client1.test);
    }


}