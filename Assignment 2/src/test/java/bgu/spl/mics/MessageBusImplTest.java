package bgu.spl.mics;

import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.*;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageBusImplTest extends TestCase {
    private MessageBusImpl magicBus;
    private ExampleEvent event1;
    private ExampleEvent event2;
    private Broadcast broadcast1;
    private Broadcast broadcast2;
    private MicroService micro1;
    private MicroService micro2;
    private MicroService micro3;

    @Before
    public void setUp() throws Exception {
        magicBus = MessageBusImpl.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSubscribeEvent() {
        //1- can't subscribe an unregistered microservice
        magicBus.subscribeEvent(event1.getClass(), micro1);
        assertFalse(magicBus.isSubscribedEvent(event1.getClass(), micro1));
        //2- successfully subscribe
        magicBus.register(micro1);
        magicBus.subscribeEvent(event1.getClass(), micro1);
        assertTrue(magicBus.isSubscribedEvent(event1.getClass(), micro1));
        //3- resubscribe
        magicBus.subscribeEvent(event1.getClass(), micro1);
        assertTrue(magicBus.isSubscribedEvent(event1.getClass(), micro1));
        //4- subscribe to another event
        magicBus.subscribeEvent(event2.getClass(), micro1);
        assertTrue(magicBus.isSubscribedEvent(event2.getClass(), micro1));
        assertTrue(magicBus.isSubscribedEvent(event2.getClass(), micro1));
    }

    @Test
    public void testSubscribeBroadcast() {
        //1- can't subscribe an unregistered microservice
        magicBus.subscribeBroadcast(broadcast1.getClass(), micro1);
        assertFalse(magicBus.isSubscribedBroadcast(broadcast1.getClass(), micro1));
        //2- successfully subscribe
        magicBus.register(micro1);
        magicBus.subscribeBroadcast(broadcast1.getClass(), micro1);
        assertTrue(magicBus.isSubscribedBroadcast(broadcast1.getClass(), micro1));
        //3- resubscribe
        magicBus.subscribeBroadcast(broadcast1.getClass(), micro1);
        assertTrue(magicBus.isSubscribedBroadcast(broadcast1.getClass(), micro1));
        //4- subscribe to another event
        magicBus.subscribeBroadcast(broadcast2.getClass(), micro1);
        assertTrue(magicBus.isSubscribedBroadcast(broadcast1.getClass(), micro1));
        assertTrue(magicBus.isSubscribedBroadcast(broadcast2.getClass(), micro1));
    }

    @Test
    public void testComplete() {
        event1 = new ExampleEvent("Student1");
        magicBus.subscribeEvent(event1.getClass() , micro1);
        Future<String> f = magicBus.sendEvent(event1);
//       Event e  = magicBus.awaitMessage(micro1); //here should be the event sent
        magicBus.complete(event1, "rigush");
        assertEquals(f.get() , "rigush");
        magicBus.complete(event1 , "nonRigush");
//        the second complete should do nothing?
        assertEquals(f.get() , "rigush");
    }

    @Test
    public void testSendBroadcast() {
        Broadcast tick = new ExampleBroadcast("1");
        magicBus.register(micro1);
        magicBus.register(micro3);
        magicBus.sendBroadcast(tick);
        try {
            Message m1 = magicBus.awaitMessage(micro1);
            assertFalse( tick == m1 ); // micro1 wont receive this message because he didnt subscribed yet
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        magicBus.subscribeBroadcast(ExampleBroadcast.class, micro1);
        magicBus.subscribeBroadcast(ExampleBroadcast.class, micro2);
        magicBus.subscribeBroadcast(ExampleBroadcast.class, micro3);
        try {
            assertEquals(magicBus.awaitMessage(micro1), tick); //checks the micro1 really received the broadcast which he subscribed for
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            assertFalse(magicBus.awaitMessage(micro2) !=  tick); //because micro2 didnt register
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        magicBus.register(micro2);
        magicBus.subscribeBroadcast(ExampleBroadcast.class, micro2);
        magicBus.sendBroadcast(tick);
        try {
            assertEquals(magicBus.awaitMessage(micro1), tick); //now micro2 is registered and subscribed so he should receive the message
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        magicBus.sendBroadcast(broadcast1);
        try {
            assertEquals(magicBus.awaitMessage(micro1), broadcast1); //checks the micro3 really received the broadcast which he subscribed for
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendEvent() {
        magicBus.register(micro1);
        magicBus.register(micro3);
        magicBus.sendEvent(event1);

        try {
            Message m1 = magicBus.awaitMessage(micro1);
            assertFalse( event1 == m1 ); // micro1 wont receive this message because he didnt subscribed yet
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        magicBus.subscribeEvent(ExampleEvent.class, micro1);
        magicBus.subscribeEvent(ExampleEvent.class, micro2);
        magicBus.subscribeEvent(event1.getClass(), micro3);
        try {
            assertEquals(magicBus.awaitMessage(micro1), event1); //checks the micro1 really received the broadcast which he subscribed for
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            assertFalse(magicBus.awaitMessage(micro2) !=  event1); //because micro2 didnt register
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        magicBus.register(micro2);
        magicBus.subscribeEvent(ExampleEvent.class, micro2);
        magicBus.sendEvent(event1);
        try {
            assertEquals(magicBus.awaitMessage(micro1), event1); //now micro2 is registered and subscribed so he should receive the message
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Event publish = new PublishResultEvent();
        magicBus.sendEvent(event1);//was event herre, changed it
        try {
            assertEquals(magicBus.awaitMessage(micro1), event1); //checks the micro3 really received the broadcast which he subscribed for, was event here cahgned it
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRegister() {
        //1- register and see it's in the microservices
        magicBus.register(micro1);
        assertTrue(magicBus.isRegistered(micro1));
        //2- if already registered - don't do anything
        magicBus.register(micro1);
        assertTrue(magicBus.isRegistered(micro1));
    }

    @Test
    public void testUnregister() {
        //1- if microService already not registered - do nothing
        magicBus.unregister(micro1);
        assertFalse(magicBus.isRegistered(micro1));

        //2- unregister and see it's not in microservice or any list in messages
        magicBus.register(micro1);
        magicBus.unregister(micro1);
        magicBus.subscribeEvent(event1.getClass(),micro1);
        magicBus.subscribeBroadcast(broadcast1.getClass(), micro1);
        magicBus.sendBroadcast(broadcast1);

        assertFalse(magicBus.isSubscribedEvent(event1.getClass(), micro1));
        assertFalse(magicBus.isSubscribedBroadcast(broadcast1.getClass(), micro1));
        assertFalse(magicBus.isRegistered(micro1));

    }
}