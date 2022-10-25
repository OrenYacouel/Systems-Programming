package bgu.spl.mics;

import junit.framework.TestCase;
import org.junit.Test;
//import sun.jvm.hotspot.tools.JStack;

import java.util.concurrent.TimeUnit;

public class FutureTest extends TestCase {
    private Future<String> future;
    private Future<String> future2;
    private String result;

    public void setUp() throws Exception {
        future = new Future<>();
        result = "hi";
    }

    public void tearDown() throws Exception {}


    @Test
    public void testGet() {
        String goat = "Messi";
        assertFalse(future.isDone());
        future.resolve(goat);
        assertTrue(future.get().equals(goat));
    }

    @Test
    public void testResolve() {
        assertFalse(future.isDone());
        String argentina = "Veron";
        future.resolve(argentina);
        assertTrue(future.get().equals(argentina));
        assertTrue(future.isDone());
    }


    @Test
    public void testIsDone() {
        assertFalse(future.isDone());
        String japan = "Kubu";
        future.resolve(japan);
        assertTrue(future.isDone());
    }

    @Test
    public void testTestGet() {
        String brazil = "FatRonaldo";
        assertFalse(future.isDone());
        future.get(90, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve(brazil);
        assertTrue(future.get(50, TimeUnit.SECONDS).equals(brazil));

    }
}