package test;

import bgu.spl.mics.Future;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {

    Future<String> future;
    Thread futureThread;
    Thread testThread;
    Runnable futureTask;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        future = new Future<>();
        testThread = new Thread(Thread.currentThread());

    }


    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void getWhileNotResolved(){

        futureTask =() -> {
            try {
                String result = future.get();
                assertEquals("Result", result); // This should only happen after the result is set
            } catch (Exception e) {
                fail("Thread was interrupted while waiting for result");
            }
        };
        futureThread = new Thread(futureTask);
        // Start the thread and give it some time to block on `future.get()`
        futureThread.start();
        try {
            Thread.sleep(500); // Allow some time for the thread to call `future.get()`
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Assert that the thread is still alive, meaning it is blocked on `future.get()`
        assertTrue(futureThread.isAlive(), "Thread should still be waiting for the result");

        // Resolve the future to unblock the thread
        future.resolve("Result");

        // Wait for the thread to finish
        try {
            futureThread.join(1000); // Allow a reasonable time for the thread to complete
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // After the future is resolved, the thread should finish execution
        assertFalse(futureThread.isAlive(), "Thread should have finished after the result was resolved");
    }


    @org.junit.jupiter.api.Test
    void resolve() {
    }

    @org.junit.jupiter.api.Test
    void isDone() {
    }

    @org.junit.jupiter.api.Test
    void testGet() {
    }
}