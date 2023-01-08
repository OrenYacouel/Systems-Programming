package bgu.spl.mics;
import static org.junit.jupiter.api.Assertions.*;


import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.jupiter.api.Test;

class MessageBusImplTest {

    private MessageBus messageBus;
    private MicroService microServiceMessage;
    private MicroService microServiceEvent;
    private MicroService microServiceBroadcast;
    private ExampleBroadcast exampleBroadcast;
    private ExampleEvent exampleEvent;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // initialize all data members required to run tests
        messageBus = MessageBusImpl.getInstance();
        microServiceMessage = new ExampleMessageSenderService("sender", new String[1]);
        microServiceEvent = new ExampleEventHandlerService("handler", new String[1]);
        microServiceBroadcast = new ExampleBroadcastListenerService("listner", new String[1] );
        exampleBroadcast = new ExampleBroadcast("broadcast");
        exampleEvent = new ExampleEvent("event");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        messageBus.unregister(microServiceMessage);
        messageBus.unregister(microServiceEvent);
        messageBus.unregister(microServiceBroadcast);
    }

    @Test
    void subscribeEvent() {
        //pre
        assertTrue(messageBus.isMicroServiceRegistered(microServiceEvent));
        //action
        int size = messageBus.getMs_EventMap().size();
        messageBus.subscribeEvent(exampleEvent.getClass(), microServiceEvent);
        //post
        assertEquals(size +1,messageBus.getMs_EventMap().size() );
        assertTrue(messageBus.isMSSubscribedToEvent(microServiceEvent, exampleEvent.getClass()));
    }

    @org.junit.jupiter.api.Test
    void subscribeBroadcast() {
        //pre
        assertTrue(messageBus.isMicroServiceRegistered(microServiceBroadcast));
        //action
        int size = messageBus.getMs_BroadcastMap().size();
        messageBus.subscribeBroadcast(exampleBroadcast.getClass(), microServiceBroadcast);
        //post
        assertEquals(size +1,messageBus.getMs_EventMap().size() );
        assertTrue(messageBus.isMSSubscribedToBroadcast(microServiceEvent, exampleBroadcast.getClass()));
    }


    @org.junit.jupiter.api.Test
    void complete() {
        //PRE
        Future<String> future = messageBus.sendEvent(exampleEvent);
        //messageBus.subscribeEvent(exampleEvent.getClass(), microServiceEvent);
        microServiceEvent.initialize();
        //act
        messageBus.complete(exampleEvent, "event");
        //POST (event value == resolved future value)
        if(future != null){
            assertEquals(future.get(), "event" );
        }
        else{
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void sendBroadcast() {
        //PRE (ms subscribed, ms not)
        messageBus.register(microServiceBroadcast);
        messageBus.register(microServiceEvent);
        microServiceBroadcast.initialize();
        microServiceEvent.initialize();
        Message subscribed_msg = null;
        Message unsubscribed_msg = null;
        //action
        messageBus.subscribeBroadcast(exampleBroadcast.getClass(), microServiceBroadcast);
        messageBus.sendBroadcast(exampleBroadcast);
        // subscribers receive message.  unsubscribed do not
        try{
            subscribed_msg = messageBus.awaitMessage(microServiceBroadcast);
            unsubscribed_msg = messageBus.awaitMessage(microServiceEvent);
        }
        catch(Exception e){
            fail();
        }
        //POST
        assertEquals(exampleBroadcast, subscribed_msg);
        assertNotEquals(exampleBroadcast, unsubscribed_msg);
    }

    @org.junit.jupiter.api.Test
    void sendEvent() {
        messageBus.register(microServiceEvent);
        messageBus.register(microServiceMessage);
        Message subscribed_event = null;
        Message unsubscribed_event = null;
        //action
        //messageBus.subscribeEvent(exampleEvent.getClass(), microServiceEvent);
        messageBus.sendEvent(exampleEvent);
        // subscribers receive event.  unsubscribed do not
        try{
            subscribed_event = messageBus.awaitMessage(microServiceEvent);
            unsubscribed_event = messageBus.awaitMessage(microServiceMessage);
        }
        catch(Exception e){
            fail();
        }
        //POST
        assertEquals(exampleEvent, subscribed_event);
        assertNotEquals(exampleEvent, unsubscribed_event);

    }
    //TODO: chovav
    @org.junit.jupiter.api.Test
    void register() {
        messageBus.register(microServiceMessage);
        assertTrue((messageBus.isMicroServiceRegistered(microServiceMessage)));
        assertFalse(messageBus.isMicroServiceRegistered(microServiceBroadcast));
    }

    @org.junit.jupiter.api.Test
    void unregister() {
        messageBus.register(microServiceMessage);
        messageBus.register(microServiceBroadcast);
        messageBus.unregister(microServiceMessage);
        assertFalse(messageBus.isMicroServiceRegistered(microServiceMessage));
        assertTrue(messageBus.isMicroServiceRegistered(microServiceBroadcast));
    }

    @org.junit.jupiter.api.Test
    void awaitMessage() {
        // reg,init ms
        messageBus.register(microServiceBroadcast);
        microServiceBroadcast.initialize();
        //action
        messageBus.sendBroadcast(exampleBroadcast);
        //check ms fetches message correctly
        try{
            assertEquals(messageBus.awaitMessage(microServiceBroadcast), exampleBroadcast);
        }
        catch(Exception e){
            fail();
        }


    }
}