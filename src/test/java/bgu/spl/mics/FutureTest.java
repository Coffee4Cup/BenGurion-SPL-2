package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class FutureTest {
    Future<Object> future;
    Thread thread;
    @BeforeEach
    void setUp() {
        future = new Future<>();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void get() {
        thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                future.resolve("test");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            assertEquals("test", future.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void getWithoutResolve() {
        final Object result = null;
        thread = new Thread(() -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                return;//should throw exception
            }
            fail("should have InterruptedException");
        });
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread.interrupt();
        assertFalse(future.isDone());
    }

    @Test
    void resolve() {

    }

    @Test
    void isDone() {

    }

    @Test
    void testGet() {

    }
}