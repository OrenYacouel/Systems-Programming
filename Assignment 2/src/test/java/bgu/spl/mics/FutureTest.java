package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {

    // initialize all data members required to run tests
    private Future<String> future;

    @BeforeEach
    void setUp() {
        future = new Future<>();
    }

    @Test
    void testGet() {
        // check PRE
        assertFalse(future.isDone());
        // MS waits for completion
        future.resolve("result");
        // retrieve result
        future.get();
        // check POST
        assertTrue(future.isDone());
    }

    @Test
    void testResolve() {
        assertNull(future.get());
        //completion of operation
        String _resolve = "result";
        future.resolve(_resolve);
        //check
        assertTrue(future.isDone());
        assertEquals(_resolve, future.get());
    }

    @Test
    void testIsDone() {
        String _resolve ="result";
        //check unresolved
        assertFalse(future.isDone());
        future.resolve(_resolve);
        //check resolve action
        assertTrue(future.isDone());
    }

    @Test
    void testGetWithTimeout() throws InterruptedException {
        //check pre
        assertFalse(future.isDone());
        String _resolve = "result";
        future.get(50, TimeUnit.MILLISECONDS);
        //return null if time expired and unresolved
        assertNull(future.get());
        future.resolve(_resolve);
        //check future is resolved
        assertEquals(future.get(50, TimeUnit.MILLISECONDS), _resolve);
    }
}